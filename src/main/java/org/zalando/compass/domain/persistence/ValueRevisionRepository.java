package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import lombok.Value;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.domain.persistence.model.tables.records.RevisionRecord;
import org.zalando.compass.domain.persistence.model.tables.records.ValueDimensionRevisionRecord;
import org.zalando.compass.domain.persistence.model.tables.records.ValueRevisionRecord;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_REVISION;
import static org.zalando.compass.library.Enums.translate;
import static org.zalando.compass.library.Tables.leftOuterJoin;
import static org.zalando.compass.library.Tables.table;

@Repository
public class ValueRevisionRepository {

    private final DSLContext db;

    @Autowired
    public ValueRevisionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final String key, final ValueRevision value) {
        final Revision revision = value.getRevision();

        final long id = db.insertInto(VALUE_REVISION)
                .columns(
                        VALUE_REVISION.REVISION,
                        VALUE_REVISION.REVISION_TYPE,
                        VALUE_REVISION.KEY_ID,
                        VALUE_REVISION.INDEX,
                        VALUE_REVISION.VALUE)
                .values(
                        val(revision.getId()),
                        val(translate(revision.getType(), RevisionType.class)),
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
                                val(revision.getId()),
                                val(dimension.getKey()),
                                val(dimension.getValue(), JsonNode.class)))
                .collect(toList());

        db.batch(queries).execute();
    }

    public List<ValueRevision> findAll(final String key, final Map<String, JsonNode> dimensions) {
        return db.select(VALUE_REVISION.fields())
                .select(REVISION.fields())
                .select(VALUE_DIMENSION_REVISION.fields())
                .from(VALUE_REVISION)
                .join(REVISION)
                .on(REVISION.ID.eq(VALUE_REVISION.REVISION))
                .leftJoin(VALUE_DIMENSION_REVISION)
                .on(VALUE_DIMENSION_REVISION.VALUE_ID.eq(VALUE_REVISION.ID))
                .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(VALUE_REVISION.REVISION))
                .where(VALUE_REVISION.KEY_ID.eq(key))
                .and(exactMatch(dimensions))
                .orderBy(REVISION.ID.desc())
                .fetchGroups(this::group, ValueDimensionRevisionRecord.class)
                .entrySet().stream()
                .map(this::mapRevision)
                .collect(toList());
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

    private Group group(final Record record) {
        return new Group(
                record.into(ValueRevisionRecord.class),
                record.into(RevisionRecord.class));
    }

    @Value
    @VisibleForTesting
    public static final class Group {
        ValueRevisionRecord value;
        RevisionRecord revision;
    }

    private ValueRevision mapRevision(final Map.Entry<Group, List<ValueDimensionRevisionRecord>> entry) {
        final ImmutableMap<String, JsonNode> dimensions = leftOuterJoin(entry.getValue(),
                ValueDimensionRevisionRecord::getDimensionId,
                ValueDimensionRevisionRecord::getDimensionValue);
        final Group group = entry.getKey();
        final RevisionRecord revision = group.getRevision();
        final ValueRevisionRecord value = group.getValue();

        return new ValueRevision(
                dimensions,
                value.getIndex(),
                new Revision(
                        revision.getId(),
                        revision.getTimestamp(),
                        translate(value.getRevisionType(), Revision.Type.class),
                        revision.getUser(),
                        revision.getComment()),
                value.getValue());
    }

}
