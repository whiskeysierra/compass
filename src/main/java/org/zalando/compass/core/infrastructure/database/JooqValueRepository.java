package org.zalando.compass.core.infrastructure.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectSeekStep2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.Values;
import org.zalando.compass.core.domain.spi.repository.ValueCriteria;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;
import org.zalando.compass.core.domain.spi.repository.lock.ValueLockRepository;
import org.zalando.compass.core.infrastructure.database.model.tables.records.ValueDimensionRecord;
import org.zalando.compass.core.infrastructure.database.model.tables.records.ValueRecord;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.zalando.compass.core.infrastructure.database.model.Tables.VALUE;
import static org.zalando.compass.core.infrastructure.database.model.Tables.VALUE_DIMENSION;
import static org.zalando.compass.library.Maps.transform;
import static org.zalando.compass.library.Tables.leftOuterJoin;
import static org.zalando.compass.library.Tables.table;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
class JooqValueRepository implements ValueRepository, ValueLockRepository {

    private final DSLContext db;
    private final JooqDimensionRepository dimensionRepository;

    @Override
    public Value create(final String key, final Value value) {
        final ValueRecord record;

        if (value.getIndex() == null) {
            record = db.insertInto(VALUE)
                    .columns(VALUE.KEY_ID, VALUE.VALUE_)
                    .values(key, value.getValue())
                    .returning(VALUE.ID, VALUE.INDEX)
                    .fetchOne();
        } else {
            record = db.insertInto(VALUE)
                    .columns(VALUE.KEY_ID, VALUE.INDEX, VALUE.VALUE_)
                    .values(key, value.getIndex(), value.getValue())
                    .returning(VALUE.ID, VALUE.INDEX)
                    .fetchOne();
        }

        final var id = record.getId();

        final List<Query> queries = value.getDimensions().entrySet().stream()
                .map(e -> db.insertInto(VALUE_DIMENSION)
                        .columns(VALUE_DIMENSION.VALUE_ID, VALUE_DIMENSION.DIMENSION_ID,
                                VALUE_DIMENSION.DIMENSION_VALUE)
                        .values(id, e.getKey().getId(), e.getValue()))
                .collect(toList());

        db.batch(queries).execute();

        return value.withIndex(record.getIndex());
    }

    @Override
    public Values findAll(final ValueCriteria criteria) {
        return doFindAll(criteria)
                .fetchGroups(ValueRecord.class, ValueDimensionRecord.class)
                .entrySet().stream()
                .map(this::map)
                .collect(collectingAndThen(toImmutableList(), Values::new));
    }

    @Override
    public Values lockAll(final ValueCriteria criteria) {
        return doFindAll(criteria)
                .forUpdate().of(VALUE)
                .fetchGroups(ValueRecord.class, ValueDimensionRecord.class)
                .entrySet().stream()
                .map(this::map)
                .collect(collectingAndThen(toImmutableList(), Values::new));
    }

    private SelectSeekStep2<Record, String, Long> doFindAll(final ValueCriteria criteria) {
        return db.select()
                .from(VALUE)
                .leftJoin(VALUE_DIMENSION)
                .on(VALUE_DIMENSION.VALUE_ID.eq(VALUE.ID))
                .where(toCondition(criteria))
                .orderBy(VALUE.KEY_ID, VALUE.INDEX);
    }

    private Collection<Condition> toCondition(final ValueCriteria criteria) {
        final List<Condition> conditions = Lists.newArrayListWithExpectedSize(2);

        if (criteria.getKey() != null) {
            conditions.add(VALUE.KEY_ID.eq(criteria.getKey()));
        }

        if (criteria.getDimension() != null) {
            conditions.add(exists(selectOne()
                    .from(VALUE_DIMENSION)
                    .where(VALUE_DIMENSION.VALUE_ID.eq(VALUE.ID))
                    .and(VALUE_DIMENSION.DIMENSION_ID.eq(criteria.getDimension().getId()))));
        }

        return conditions;
    }

    @Override
    public Optional<Value> lock(final String key, final Map<Dimension, JsonNode> dimensions) {
        return db.select()
                .from(VALUE)
                .leftJoin(VALUE_DIMENSION)
                .on(VALUE.ID.eq(VALUE_DIMENSION.VALUE_ID))
                .where(VALUE.KEY_ID.eq(key))
                .and(exactMatch(dimensions))
                .forUpdate().of(VALUE)
                .fetchGroups(ValueRecord.class, ValueDimensionRecord.class)
                .entrySet().stream()
                .map(this::map)
                .collect(toOptional());
    }

    private Value map(final Entry<ValueRecord, List<ValueDimensionRecord>> entry) {
        final var row = entry.getKey();
        final var dimensions = leftOuterJoin(entry.getValue(),
                ValueDimensionRecord::getDimensionId,
                ValueDimensionRecord::getDimensionValue);

        return new Value(
                // TODO delegate properly
                transform(dimensions, id -> dimensionRepository.find(id).orElseThrow(NotFoundException::new)),
                row.getIndex(),
                row.getValue());
    }

    @Override
    public void update(final String key, final Value value) {
        db.update(VALUE)
                .set(VALUE.INDEX, value.getIndex())
                .set(VALUE.VALUE_, value.getValue())
                .where(VALUE.KEY_ID.eq(key))
                .and(exactMatch(value.getDimensions()))
                .returning(VALUE.ID)
                .fetchOne().getId();
    }

    @Override
    public void delete(final String key, final Map<Dimension, JsonNode> dimensions) {
        db.deleteFrom(VALUE)
                .where(VALUE.KEY_ID.eq(key))
                .and(exactMatch(dimensions))
                .execute();
    }

    private Condition exactMatch(final Map<Dimension, JsonNode> dimensions) {
        if (dimensions.isEmpty()) {
            return notExists(selectOne()
                    .from(VALUE_DIMENSION)
                    .where(VALUE_DIMENSION.VALUE_ID.eq(VALUE.ID)));
        } else {
            return notExists(selectOne()
                    .from(table(transform(dimensions, Dimension::getId), String.class, JsonNode.class)
                            .as("expected", "dimension_id", "dimension_value"))
                    .fullOuterJoin(select(VALUE_DIMENSION.DIMENSION_ID, VALUE_DIMENSION.DIMENSION_VALUE)
                            .from(VALUE_DIMENSION)
                            .where(VALUE_DIMENSION.VALUE_ID.eq(VALUE.ID))
                            .asTable("actual"))
                    .using(field("dimension_id"), field("dimension_value"))
                    .where(field("actual.dimension_id").isNull())
                    .or(field("expected.dimension_id").isNull()));
        }
    }

}
