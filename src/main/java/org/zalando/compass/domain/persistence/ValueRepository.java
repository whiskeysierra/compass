package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectSeekStep2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.model.tables.records.ValueDimensionRecord;
import org.zalando.compass.domain.persistence.model.tables.records.ValueRecord;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.stream.Collectors.toList;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.zalando.compass.domain.persistence.ValueCriteria.byDimension;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_DIMENSION;
import static org.zalando.compass.library.Tables.table;

@Repository
public class ValueRepository {

    private final DSLContext db;

    @Autowired
    public ValueRepository(final DSLContext db) {
        this.db = db;
    }

    public Value create(final String key, final Value value) {
        final ValueRecord record;

        // TODO ugly!
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

        final Long id = record.getId();

        final List<Query> queries = value.getDimensions().entrySet().stream()
                .map(e -> db.insertInto(VALUE_DIMENSION)
                        .columns(VALUE_DIMENSION.VALUE_ID, VALUE_DIMENSION.DIMENSION_ID,
                                VALUE_DIMENSION.DIMENSION_VALUE)
                        .values(id, e.getKey(), e.getValue()))
                .collect(toList());

        db.batch(queries).execute();

        return value.withIndex(record.getIndex());
    }

    public List<Value> findAll(final ValueCriteria criteria) {
        return doFindAll(criteria)
                .fetchGroups(ValueRecord.class, ValueDimensionRecord.class)
                .entrySet().stream()
                .map(this::map)
                .collect(toList());
    }

    public Multimap<String, Value> lockAll(final String dimension) {
        final ImmutableMultimap.Builder<String, Value> builder = ImmutableMultimap.builder();
        doFindAll(byDimension(dimension))
                .forUpdate().of(VALUE)
                .fetchGroups(VALUE.KEY_ID)
                .forEach((key, result) ->
                        result.intoGroups(ValueRecord.class, ValueDimensionRecord.class)
                                .entrySet().stream()
                                .map(this::map)
                                .forEach(value -> builder.put(key, value)));
        return builder.build();
    }

    public List<Value> lockAll(final ValueCriteria criteria) {
        return doFindAll(criteria)
                .forUpdate().of(VALUE)
                .fetchGroups(ValueRecord.class, ValueDimensionRecord.class)
                .entrySet().stream()
                .map(this::map)
                .collect(toList());
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
                    .and(VALUE_DIMENSION.DIMENSION_ID.eq(criteria.getDimension()))));
        }

        return conditions;
    }

    public Optional<Value> lock(final String key, final Map<String, JsonNode> dimensions) {
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
        final ValueRecord row = entry.getKey();

        final List<ValueDimensionRecord> result = entry.getValue();
        final ImmutableMap<String, JsonNode> dimensions = toMap(result);

        return new Value(
                dimensions,
                row.getIndex(),
                row.getValue());
    }

    private ImmutableMap<String, JsonNode> toMap(final List<ValueDimensionRecord> result) {
        if (result.size() == 1) {
            final ValueDimensionRecord record = result.get(0);

            // empty left join
            if (record.getDimensionId() == null) {
                return ImmutableMap.of();
            }
        }

        return result.stream().collect(toImmutableMap(
                ValueDimensionRecord::getDimensionId,
                ValueDimensionRecord::getDimensionValue));
    }

    public void update(final String key, final Value value) {
        db.update(VALUE)
                .set(VALUE.INDEX, value.getIndex())
                .set(VALUE.VALUE_, value.getValue())
                .where(VALUE.KEY_ID.eq(key))
                .and(exactMatch(value.getDimensions()))
                .returning(VALUE.ID)
                .fetchOne().getId();
    }

    public void delete(final String key, final Map<String, JsonNode> dimensions) {
        db.deleteFrom(VALUE)
                .where(VALUE.KEY_ID.eq(key))
                .and(exactMatch(dimensions))
                .execute();
    }

    private Condition exactMatch(final Map<String, JsonNode> dimensions) {
        if (dimensions.isEmpty()) {
            return notExists(selectOne()
                    .from(VALUE_DIMENSION)
                    .where(VALUE_DIMENSION.VALUE_ID.eq(VALUE.ID)));
        } else {
            return notExists(selectOne()
                    .from(table(dimensions, String.class, JsonNode.class)
                            .as("expected", "dimension_id", "dimension_value"))
                    .fullOuterJoin(select(VALUE_DIMENSION.DIMENSION_ID, VALUE_DIMENSION.DIMENSION_VALUE)
                            .from(VALUE_DIMENSION)
                            .where(VALUE_DIMENSION.VALUE_ID.eq(VALUE.ID))
                            .asTable("actual"))
                    .using(field("dimension_id"), field("dimension_value"))
                    // TODO find out why coalesce doesn't work here
                    .where(field("actual.dimension_id").isNull())
                    .or(field("expected.dimension_id").isNull()));
        }
    }

}
