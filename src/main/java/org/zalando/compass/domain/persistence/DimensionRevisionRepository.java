package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;

@Repository
public class DimensionRevisionRepository {

    private final DSLContext db;

    @Autowired
    public DimensionRevisionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final Dimension dimension, final Revision revision) {
        db.insertInto(DIMENSION_REVISION)
                .columns(DIMENSION_REVISION.ID,
                        DIMENSION_REVISION.REVISION,
                        DIMENSION_REVISION.REVISION_TYPE,
                        DIMENSION_REVISION.SCHEMA,
                        DIMENSION_REVISION.RELATION,
                        DIMENSION_REVISION.DESCRIPTION)
                .values(dimension.getId(),
                        revision.getId(),
                        map(revision.getType(), RevisionType.class),
                        dimension.getSchema(),
                        dimension.getRelation(),
                        dimension.getDescription())
                .execute();
    }

    public Page<DimensionRevision> findAll(final String id, final int limit, @Nullable final Long after) {
        final List<DimensionRevision> revisions = db.select(DIMENSION_REVISION.fields())
                .select(REVISION.fields())
                .from(DIMENSION_REVISION)
                .join(REVISION).on(REVISION.ID.eq(DIMENSION_REVISION.REVISION))
                .where(DIMENSION_REVISION.ID.eq(id))
                .orderBy(REVISION.ID.desc())
                .seekAfter(firstNonNull(after, Long.MAX_VALUE)) // TODO find a better way
                .limit(limit + 1)
                .fetch().map(this::mapRevision);

        // TODO library
        if (revisions.size() > limit) {
            final List<DimensionRevision> items = revisions.subList(0, limit);
            final DimensionRevision next = items.get(items.size() - 1);
            return new Page<>(items, next);
        } else {
            return new Page<>(revisions, null);
        }
    }

    public Optional<DimensionRevision> find(final String id, final long revision) {
        return db.select(DIMENSION_REVISION.fields())
                .select(REVISION.fields())
                .from(DIMENSION_REVISION)
                .join(REVISION).on(REVISION.ID.eq(DIMENSION_REVISION.REVISION))
                .where(DIMENSION_REVISION.ID.eq(id))
                .and(REVISION.ID.eq(revision))
                .fetchOptional()
                .map(this::mapRevision);
    }

    private DimensionRevision mapRevision(final Record record) {
        return new DimensionRevision(
                record.get(DIMENSION_REVISION.ID),
                new Revision(
                        record.get(REVISION.ID),
                        record.get(REVISION.TIMESTAMP),
                        map(record.get(DIMENSION_REVISION.REVISION_TYPE), Revision.Type.class),
                        record.get(REVISION.USER),
                        record.get(REVISION.COMMENT)
                ),
                record.get(DIMENSION_REVISION.SCHEMA),
                record.get(DIMENSION_REVISION.RELATION),
                record.get(DIMENSION_REVISION.DESCRIPTION)
        );
    }

    private <A extends Enum<A>, B extends Enum<B>> B map(final A value, final Class<B> type) {
        return Enum.valueOf(type, value.name());
    }

}
