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
import org.zalando.compass.library.Enums;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.KEY_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_DIMENSION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_REVISION;
import static org.zalando.compass.library.Enums.translate;
import static org.zalando.compass.library.Tables.table;

@Repository
public class ValueRevisionRepository {

    private final DSLContext db;

    @Autowired
    public ValueRevisionRepository(final DSLContext db) {
        this.db = db;
    }

    // TODO assumes that we're called after the actual insert/update/delete
    public void create(final String key, final ValueRevision value) {
        final Revision revision = value.getRevision();

        db.insertInto(VALUE_REVISION)
                .columns(
                        VALUE_REVISION.ID,
                        VALUE_REVISION.REVISION,
                        VALUE_REVISION.REVISION_TYPE,
                        VALUE_REVISION.KEY_ID,
                        VALUE_REVISION.KEY_REVISION,
                        VALUE_REVISION.INDEX,
                        VALUE_REVISION.VALUE)
                .values(
                        val(value.getId()),
                        val(revision.getId()),
                        val(translate(revision.getType(), RevisionType.class)),
                        val(key),
                        select(max(KEY_REVISION.REVISION))
                                .from(KEY_REVISION)
                                .where(KEY_REVISION.ID.eq(key)).asField(),
                        val(value.getIndex()),
                        val(value.getValue(), JsonNode.class))
                .execute();

        final List<Query> queries = value.getDimensions().entrySet().stream()
                .map(dimension -> db.insertInto(VALUE_DIMENSION_REVISION)
                        .columns(
                                VALUE_DIMENSION_REVISION.VALUE_ID,
                                VALUE_DIMENSION_REVISION.VALUE_REVISION,
                                VALUE_DIMENSION_REVISION.DIMENSION_ID,
                                VALUE_DIMENSION_REVISION.DIMENSION_REVISION,
                                VALUE_DIMENSION_REVISION.DIMENSION_VALUE)
                        .values(val(value.getId()),
                                val(revision.getId()),
                                val(dimension.getKey()),
                                // TODO do it once?!
                                select(max(DIMENSION_REVISION.REVISION))
                                        .from(DIMENSION_REVISION)
                                        .where(DIMENSION_REVISION.ID.eq(dimension.getKey())).asField(),
                                val(dimension.getValue(), JsonNode.class)))
                .collect(toList());

        db.batch(queries).execute();
    }

    public List<ValueRevision> findAll(final String key, final Map<String, JsonNode> dimensions) {
        return findLatestValueId(key, dimensions)
                .map(id -> db.select(VALUE_REVISION.fields())
                        .select(REVISION.fields())
                        .select(VALUE_DIMENSION_REVISION.fields())
                        .from(VALUE_REVISION)
                        .join(REVISION)
                        .on(REVISION.ID.eq(VALUE_REVISION.REVISION))
                        .leftJoin(VALUE_DIMENSION_REVISION)
                        .on(VALUE_DIMENSION_REVISION.VALUE_ID.eq(VALUE_REVISION.ID))
                        .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(VALUE_REVISION.REVISION))
                        .where(VALUE_REVISION.ID.eq(id))
                        .orderBy(REVISION.ID.desc())
                        .fetchGroups(this::group, ValueDimensionRevisionRecord.class)
                        .entrySet().stream()
                        .map(this::mapRevision)
                        .collect(toList()))
                .orElse(emptyList());
    }

    public Optional<ValueRevision> find(final String key, final Map<String, JsonNode> dimensions, final long revision) {
        return findLatestValueId(key, dimensions)
                .flatMap(id -> db.select(VALUE_REVISION.fields())
                        .select(REVISION.fields())
                        .select(VALUE_DIMENSION_REVISION.fields())
                        .from(VALUE_REVISION)
                        .join(REVISION)
                        .on(REVISION.ID.eq(VALUE_REVISION.REVISION))
                        .leftJoin(VALUE_DIMENSION_REVISION)
                        .on(VALUE_DIMENSION_REVISION.VALUE_ID.eq(VALUE_REVISION.ID))
                        .and(VALUE_DIMENSION_REVISION.VALUE_REVISION.eq(VALUE_REVISION.REVISION))
                        .where(VALUE_REVISION.ID.eq(id))
                        .and(VALUE_REVISION.REVISION.eq(revision))
                        .fetchGroups(this::group, ValueDimensionRevisionRecord.class)
                        .entrySet().stream()
                        .map(this::mapRevision)
                        .collect(toOptional()));
    }

    private Optional<Long> findLatestValueId(final String key, final Map<String, JsonNode> dimensions) {
        return db.select(VALUE_REVISION.ID)
                .from(VALUE_REVISION)
                .where(VALUE_REVISION.KEY_ID.eq(key))
                .and(exactMatch(dimensions))
                .orderBy(VALUE_REVISION.REVISION.desc())
                .limit(1)
                .fetchOptionalInto(Long.class);
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
                    // TODO find out why coalesce doesn't work here
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
        final Group group = entry.getKey();
        final ImmutableMap<String, JsonNode> dimensions = toMap(entry.getValue());
        final RevisionRecord revision = group.getRevision();
        final ValueRevisionRecord value = group.getValue();

        return new ValueRevision(
                value.getId(),
                dimensions,
                value.getIndex(),
                new Revision(
                        revision.getId(),
                        revision.getTimestamp(),
                        Enums.translate(value.getRevisionType(), Revision.Type.class),
                        revision.getUser(),
                        revision.getComment()),
                value.getValue());
    }

    private ImmutableMap<String, JsonNode> toMap(final List<ValueDimensionRevisionRecord> result) {
        if (result.size() == 1) {
            final ValueDimensionRevisionRecord record = result.get(0);

            // empty left join
            if (record.getDimensionId() == null) {
                return ImmutableMap.of();
            }
        }

        return result.stream().collect(toImmutableMap(
                ValueDimensionRevisionRecord::getDimensionId,
                ValueDimensionRevisionRecord::getDimensionValue));
    }

}
