package org.zalando.compass.domain.persistence;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.jooq.impl.DSL.trueCondition;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;

@Repository
public class DimensionRepository {

    private final DSLContext db;

    @Autowired
    public DimensionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final Dimension dimension, final Revision revision) {
        db.insertInto(DIMENSION)
                .columns(DIMENSION.ID, DIMENSION.SCHEMA, DIMENSION.RELATION, DIMENSION.DESCRIPTION)
                .values(dimension.getId(), dimension.getSchema(), dimension.getRelation(),
                        dimension.getDescription())
                .execute();

        createRevision(dimension, revision);
    }

    public List<Dimension> lockAll(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        return doFindAll(dimensions)
                .forUpdate()
                .fetchInto(Dimension.class);
    }

    public List<Dimension> findAll(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        return doFindAll(dimensions)
                .fetchInto(Dimension.class);
    }

    private SelectSeekStep1<Record, String> doFindAll(final Set<String> dimensions) {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(DIMENSION.ID.in(dimensions))
                .orderBy(DIMENSION.ID.asc());
    }

    public List<Dimension> findAll(@Nullable final String term) {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(toCondition(term))
                .orderBy(DIMENSION.ID.asc())
                .fetchInto(Dimension.class);
    }

    private Condition toCondition(@Nullable final String term) {
        if (term == null) {
            return trueCondition();
        }

        return DIMENSION.ID.likeIgnoreCase("%" + term + "%")
                .or(DIMENSION.DESCRIPTION.likeIgnoreCase("%" + term + "%"));
    }

    public Optional<Dimension> find(final String id) {
        return doFind(id)
                .fetchOptionalInto(Dimension.class);
    }

    public Optional<Dimension> lock(final String id) {
        return doFind(id)
                .forUpdate()
                .fetchOptionalInto(Dimension.class);
    }

    private SelectConditionStep<Record> doFind(final String id) {
        return db.select()
                .from(DIMENSION)
                .where(DIMENSION.ID.eq(id));
    }

    public List<DimensionRevision> findAllRevisions(final String id) {
        return db.select(DIMENSION_REVISION.fields())
                .select(REVISION.fields())
                .from(DIMENSION_REVISION)
                .join(REVISION).on(REVISION.ID.eq(DIMENSION_REVISION.REVISION))
                .where(DIMENSION_REVISION.ID.eq(id))
                .orderBy(REVISION.ID.desc())
                .fetch().map(this::mapRevision);
    }

    public Optional<DimensionRevision> findRevision(final String id, final long revision) {
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

    public void update(final Dimension dimension, final Revision revision) {
        db.update(DIMENSION)
                .set(DIMENSION.SCHEMA, dimension.getSchema())
                .set(DIMENSION.RELATION, dimension.getRelation())
                .set(DIMENSION.DESCRIPTION, dimension.getDescription())
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();

        createRevision(dimension, revision);
    }

    private <A extends Enum<A>, B extends Enum<B>> B map(final A value, final Class<B> type) {
        return Enum.valueOf(type, value.name());
    }

    public void delete(final Dimension dimension, final Revision revision) {
        db.deleteFrom(DIMENSION)
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();

        createRevision(dimension, revision);
    }

    private void createRevision(final Dimension dimension, final Revision revision) {
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

}
