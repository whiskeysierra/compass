package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.library.Pages;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;

@Repository
public class DimensionRevisionRepository {

    private static final org.zalando.compass.domain.persistence.model.tables.DimensionRevision SELF =
            DIMENSION_REVISION.as("self");

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
                        dimension.getRevision().getType(),
                        dimension.getSchema(),
                        dimension.getRelation(),
                        dimension.getDescription())
                .execute();
    }

    public Page<Revision> findPageRevisions(final int limit, @Nullable final Long after) {
        final List<Revision> revisions = db.select(REVISION.fields())
                .from(REVISION)
                .where(exists(selectOne()
                        .from(DIMENSION_REVISION)
                        .where(DIMENSION_REVISION.REVISION.eq(REVISION.ID))
                        .and(trueCondition())))
                .orderBy(REVISION.ID.desc())
                // TODO .seekAfter(after == null ? null : val(after, Long.class))
                .limit(limit + 1)
                .fetch().map(this::mapRevisionWithoutType);

        return Pages.page(revisions, limit);
    }

    public Optional<PageRevision<Dimension>> findPage(final long revisionId) {
        // TODO move to revisionrepository
        final Optional<Revision> optional = db.select(REVISION.fields())
                .from(REVISION)
                .where(REVISION.ID.eq(revisionId))
                .fetchOptional(this::mapRevisionWithoutType);

        // TODO move to service layer
        return optional.map(revision -> {
            final List<Dimension> dimensions = db.select(DIMENSION_REVISION.fields())
                    .from(DIMENSION_REVISION)
                    .where(DIMENSION_REVISION.REVISION_TYPE.ne(RevisionType.DELETE))
                    .and(DIMENSION_REVISION.REVISION.eq(select(max(SELF.REVISION))
                            .from(SELF)
                            .where(SELF.ID.eq(DIMENSION_REVISION.ID))
                            .and(SELF.REVISION.le(revisionId))))
                    .fetchInto(Dimension.class);

            return new PageRevision<>(revision, dimensions);
        });
    }

    public Page<Revision> findRevisions(final String id, final int limit, @Nullable final Long after) {
        final List<Revision> revisions = db.select(REVISION.fields())
                .select(DIMENSION_REVISION.fields())
                .from(REVISION)
                .join(DIMENSION_REVISION).on(DIMENSION_REVISION.REVISION.eq(REVISION.ID))
                .where(DIMENSION_REVISION.ID.eq(id))
                .orderBy(REVISION.ID.desc())
                .seekAfter(after == null ? null : val(after, Long.class))
                .limit(limit + 1)
                .fetch().map(this::mapRevisionWithType);

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
                mapRevisionWithType(record),
                record.get(DIMENSION_REVISION.SCHEMA),
                record.get(DIMENSION_REVISION.RELATION),
                record.get(DIMENSION_REVISION.DESCRIPTION)
        );
    }

    private Revision mapRevisionWithType(final Record record) {
        return new Revision(
                        record.get(REVISION.ID),
                        record.get(REVISION.TIMESTAMP).atOffset(UTC),
                        record.get(DIMENSION_REVISION.REVISION_TYPE),
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
