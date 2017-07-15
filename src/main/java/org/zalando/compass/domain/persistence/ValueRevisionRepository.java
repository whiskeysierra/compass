package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ObjectArrays;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.domain.persistence.model.tables.records.ValueDimensionRevisionRecord;
import org.zalando.compass.domain.persistence.model.tables.records.ValueRevisionRecord;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;
import static org.jooq.impl.DSL.arrayAgg;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_REVISION;
import static org.zalando.compass.library.Tables.leftOuterJoin;
import static org.zalando.compass.library.Tables.table;

@Repository
public class ValueRevisionRepository {

    private static final org.zalando.compass.domain.persistence.model.tables.ValueRevision SELF =
            VALUE_REVISION.as("self");

    private final DSLContext db;

    @Autowired
    public ValueRevisionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final String key, final ValueRevision value) {
        final long id = db.insertInto(VALUE_REVISION)
                .columns(
                        VALUE_REVISION.REVISION,
                        VALUE_REVISION.REVISION_TYPE,
                        VALUE_REVISION.KEY_ID,
                        VALUE_REVISION.INDEX,
                        VALUE_REVISION.VALUE)
                .values(
                        val(value.getRevision().getId()),
                        val(value.getRevision().getType()),
                        val(key),
                        val(value.getIndex()),
                        val(value.getValue(), JsonNode.class))
                .returning(VALUE_REVISION.ID)
                .fetchOne().getId();

        final List<Query> queries = value.getDimensions().entrySet().stream()
                .map(dimension -> db.insertInto(VALUE_DIMENSION_REVISION)
                        .columns(
                                VALUE_DIMENSION_REVISION.VALUE_ID,
                                VALUE_DIMENSION_REVISION.VALUE_REVISION,
                                VALUE_DIMENSION_REVISION.DIMENSION_ID,
                                VALUE_DIMENSION_REVISION.DIMENSION_VALUE)
                        .values(val(id),
                                val(value.getRevision().getId()),
                                val(dimension.getKey()),
                                val(dimension.getValue(), JsonNode.class)))
                .collect(toList());

        db.batch(queries).execute();
    }

    public List<Revision> findPageRevisions(final String key, final Pagination<Long> query) {
        return query.seek(db.select(REVISION.fields())
                .from(REVISION)
                .where(exists(selectOne()
                        .from(VALUE_REVISION)
                        .where(VALUE_REVISION.KEY_ID.eq(key))
                        .and(VALUE_REVISION.REVISION.eq(REVISION.ID)))), REVISION.ID, SortOrder.DESC)
                .fetch(this::mapRevisionWithoutType);
    }

    public List<ValueRevision> findPage(final String key, final long revisionId) {
        return find(key, revisionId, VALUE_REVISION.REVISION_TYPE.ne(RevisionType.DELETE));
    }

    public List<ValueRevision> findValueRevisions(final String key, final long revisionId) {
        return find(key, revisionId, trueCondition());
    }

    private List<ValueRevision> find(final String key, final long revisionId, final Condition condition) {
        final Map<Record, List<ValueDimensionRevisionRecord>> map = db
                .select(VALUE_REVISION.fields())
                .select(REVISION.fields())
                .select(VALUE_DIMENSION_REVISION.fields())
                .from(VALUE_REVISION)
                .join(REVISION).on(REVISION.ID.eq(VALUE_REVISION.REVISION))
                .leftJoin(VALUE_DIMENSION_REVISION)
                .on(VALUE_DIMENSION_REVISION.VALUE_ID.eq(VALUE_REVISION.ID))
                .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(VALUE_REVISION.REVISION))
                .where(VALUE_REVISION.KEY_ID.eq(key))
                .and(condition)
                .and(VALUE_REVISION.REVISION.eq(select(max(SELF.REVISION))
                        .from(SELF)
                        .where(SELF.KEY_ID.eq(VALUE_REVISION.KEY_ID))
                        .and(SELF.REVISION.le(revisionId))
                        .and(dimensionsOf(SELF).isNotDistinctFrom(dimensionsOf(VALUE_REVISION)))))
                .orderBy(VALUE_REVISION.INDEX)
                .fetchGroups(
                        ObjectArrays.concat(VALUE_REVISION.fields(), REVISION.fields(), Field.class),
                        ValueDimensionRevisionRecord.class);

        return map.entrySet().stream()
                .map(entry -> {
                    final ValueRevisionRecord value = entry.getKey().into(ValueRevisionRecord.class);
                    final Revision revision = mapRevisionWithType(entry.getKey());

                    final ImmutableMap<String, JsonNode> dimensions = leftOuterJoin(
                            entry.getValue(),
                            ValueDimensionRevisionRecord::getDimensionId,
                            ValueDimensionRevisionRecord::getDimensionValue);

                    return new ValueRevision(
                            dimensions,
                            value.getIndex(),
                            revision,
                            value.getValue());
                })
                .collect(toList());
    }

    private <T> Field<T> dimensionsOf(final org.zalando.compass.domain.persistence.model.tables.ValueRevision ref) {
        return select(arrayAgg(field(name("t"))))
                .from(select(VALUE_DIMENSION_REVISION.DIMENSION_ID, VALUE_DIMENSION_REVISION.DIMENSION_VALUE)
                        .from(VALUE_DIMENSION_REVISION)
                        .where(VALUE_DIMENSION_REVISION.VALUE_ID.eq(ref.ID))
                        .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(ref.REVISION)).asTable("t")).asField();
    }

    public List<Revision> findRevisions(final String key, final Map<String, JsonNode> dimensions,
            final Pagination<Long> query) {

        return query.seek(db.select(REVISION.fields())
                .select(VALUE_REVISION.REVISION_TYPE)
                .from(REVISION)
                .join(VALUE_REVISION).on(VALUE_REVISION.REVISION.eq(REVISION.ID))
                .where(VALUE_REVISION.KEY_ID.eq(key))
                .and(exactMatch(dimensions)), REVISION.ID, SortOrder.DESC)
                .fetch(this::mapRevisionWithType);
    }

    private Condition exactMatch(final Map<String, JsonNode> dimensions) {
        if (dimensions.isEmpty()) {
            return notExists(selectOne()
                    .from(VALUE_DIMENSION_REVISION)
                    .where(VALUE_DIMENSION_REVISION.VALUE_ID.eq(VALUE_REVISION.ID))
                    .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(VALUE_REVISION.REVISION)));
        } else {
            return notExists(selectOne()
                    .from(table(dimensions, String.class, JsonNode.class)
                            .as("expected", "dimension_id", "dimension_value"))
                    .fullOuterJoin(
                            select(VALUE_DIMENSION_REVISION.DIMENSION_ID, VALUE_DIMENSION_REVISION.DIMENSION_VALUE)
                                    .from(VALUE_DIMENSION_REVISION)
                                    .where(VALUE_DIMENSION_REVISION.VALUE_ID.eq(VALUE_REVISION.ID))
                                    .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(VALUE_REVISION.REVISION))
                                    .asTable("actual"))
                    .using(field("dimension_id"), field("dimension_value"))
                    .where(field("actual.dimension_id").isNull())
                    .or(field("expected.dimension_id").isNull()));
        }
    }

    private Revision mapRevisionWithType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).atOffset(UTC),
                record.get(VALUE_REVISION.REVISION_TYPE),
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

    private Revision mapRevisionWithoutType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).atOffset(UTC),
                null,
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
