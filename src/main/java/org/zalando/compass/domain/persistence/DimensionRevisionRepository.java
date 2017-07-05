package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.library.Pages;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.library.Enums.translate;

@Repository
public class DimensionRevisionRepository {

    private final DSLContext db;

    @Autowired
    public DimensionRevisionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final DimensionRevision dimension) {
        db.insertInto(DIMENSION_REVISION)
                .columns(DIMENSION_REVISION.ID,
                        DIMENSION_REVISION.REVISION,
                        DIMENSION_REVISION.REVISION_TYPE,
                        DIMENSION_REVISION.SCHEMA,
                        DIMENSION_REVISION.RELATION,
                        DIMENSION_REVISION.DESCRIPTION)
                .values(dimension.getId(),
                        dimension.getRevision().getId(),
                        translate(dimension.getRevision().getType(), RevisionType.class),
                        dimension.getSchema(),
                        dimension.getRelation(),
                        dimension.getDescription())
                .execute();
    }

    public Page<Revision> findAll(final String id, final int limit, @Nullable final Long after) {
        final List<Revision> revisions = db.select(REVISION.fields())
                .select(DIMENSION_REVISION.REVISION_TYPE)
                .from(REVISION)
                .join(DIMENSION_REVISION).on(DIMENSION_REVISION.REVISION.eq(REVISION.ID))
                .where(DIMENSION_REVISION.ID.eq(id))
                .orderBy(REVISION.ID.desc())
                .seekAfter(firstNonNull(after, Long.MAX_VALUE)) // TODO find a better way
                .limit(limit + 1)
                .fetch().map(this::mapRevision);

        return Pages.page(revisions, limit);
    }

    public Optional<DimensionRevision> find(final String id, final long revision) {
        return db.select(DIMENSION_REVISION.fields())
                .select(REVISION.fields())
                .from(DIMENSION_REVISION)
                .join(REVISION).on(REVISION.ID.eq(DIMENSION_REVISION.REVISION))
                .where(DIMENSION_REVISION.ID.eq(id))
                .and(REVISION.ID.eq(revision))
                .fetchOptional()
                .map(this::mapDimension);
    }

    private DimensionRevision mapDimension(final Record record) {
        return new DimensionRevision(
                record.get(DIMENSION_REVISION.ID),
                mapRevision(record),
                record.get(DIMENSION_REVISION.SCHEMA),
                record.get(DIMENSION_REVISION.RELATION),
                record.get(DIMENSION_REVISION.DESCRIPTION)
        );
    }

    private Revision mapRevision(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP),
                translate(record.get(DIMENSION_REVISION.REVISION_TYPE), Revision.Type.class),
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
