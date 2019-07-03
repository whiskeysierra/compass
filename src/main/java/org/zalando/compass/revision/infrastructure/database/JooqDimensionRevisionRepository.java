package org.zalando.compass.revision.infrastructure.database;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.spi.repository.RelationRepository;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;

import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.zalando.compass.core.infrastructure.database.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.core.infrastructure.database.model.Tables.REVISION;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
class JooqDimensionRevisionRepository implements DimensionRevisionRepository {

    private static final org.zalando.compass.core.infrastructure.database.model.tables.DimensionRevision SELF =
            DIMENSION_REVISION.as("self");

    private final DSLContext db;
    private final RelationRepository repository;

    @Override
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
                        dimension.getRelation().getId(),
                        dimension.getDescription())
                .execute();
    }

    @Override
    public List<Revision> findPageRevisions(final Pagination<Long> query) {
        final SelectConditionStep<Record> where = db.select(REVISION.fields())
                .from(REVISION)
                .where(exists(selectOne()
                        .from(DIMENSION_REVISION)
                        .where(DIMENSION_REVISION.REVISION.eq(REVISION.ID))));

        return query.seek(where, REVISION.ID, SortOrder.DESC)
                .fetch().map(this::mapRevisionWithoutType);
    }

    @Override
    public List<Dimension> findPage(final long revisionId, final Pagination<String> query) {
        return query.seek(db.select(asList(
                DIMENSION_REVISION.ID,
                DIMENSION_REVISION.SCHEMA,
                DIMENSION_REVISION.RELATION,
                DIMENSION_REVISION.DESCRIPTION))
                .from(DIMENSION_REVISION)
                .where(DIMENSION_REVISION.REVISION_TYPE.ne(RevisionType.DELETE))
                .and(DIMENSION_REVISION.REVISION.eq(select(max(SELF.REVISION))
                        .from(SELF)
                        .where(SELF.ID.eq(DIMENSION_REVISION.ID))
                        .and(SELF.REVISION.le(revisionId)))), DIMENSION_REVISION.ID, SortOrder.ASC)
                .fetch(this::map);
    }

    private Dimension map(final Record record) {
        return new Dimension(
                record.get(DIMENSION_REVISION.ID),
                record.get(DIMENSION_REVISION.SCHEMA),
                repository.find(record.get(DIMENSION_REVISION.RELATION)).orElseThrow(NotFoundException::new),
                record.get(DIMENSION_REVISION.DESCRIPTION)
        );
    }

    @Override
    public List<Revision> findRevisions(final String id, final Pagination<Long> query) {
        return query.seek(db.select(REVISION.fields())
                .select(DIMENSION_REVISION.fields())
                .from(REVISION)
                .join(DIMENSION_REVISION).on(DIMENSION_REVISION.REVISION.eq(REVISION.ID))
                .where(DIMENSION_REVISION.ID.eq(id)), REVISION.ID, SortOrder.DESC)
                .fetch(this::mapRevisionWithType);
    }

    @Override
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
                // TODO delegate in a cleaner way
                repository.find(record.get(DIMENSION_REVISION.RELATION)).orElseThrow(NotFoundException::new),
                record.get(DIMENSION_REVISION.DESCRIPTION)
        );
    }

    private Revision mapRevisionWithType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).withOffsetSameInstant(UTC),
                record.get(DIMENSION_REVISION.REVISION_TYPE),
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

    private Revision mapRevisionWithoutType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).withOffsetSameInstant(UTC),
                null,
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
