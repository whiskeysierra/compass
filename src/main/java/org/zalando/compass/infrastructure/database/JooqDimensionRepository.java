package org.zalando.compass.infrastructure.database;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.repository.DimensionGuard;
import org.zalando.compass.domain.repository.DimensionRepository;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.jooq.impl.DSL.trueCondition;
import static org.zalando.compass.infrastructure.database.model.Tables.DIMENSION;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
class JooqDimensionRepository implements DimensionRepository, DimensionGuard {

    private final DSLContext db;

    @Override
    public void create(final Dimension dimension) {
        db.insertInto(DIMENSION)
                .columns(DIMENSION.ID, DIMENSION.SCHEMA, DIMENSION.RELATION, DIMENSION.DESCRIPTION)
                .values(dimension.getId(), dimension.getSchema(), dimension.getRelation(),
                        dimension.getDescription())
                .execute();
    }

    @Override
    public List<Dimension> lockAll(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        return doFindAll(dimensions)
                .forUpdate()
                .fetchInto(Dimension.class);
    }

    @Override
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

    @Override
    public List<Dimension> findAll(@Nullable final String term, final Pagination<String> query) {
        return query.seek(db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(toCondition(term)), DIMENSION.ID, SortOrder.ASC)
                .fetchInto(Dimension.class);
    }

    private Condition toCondition(@Nullable final String term) {
        if (term == null) {
            return trueCondition();
        }

        return DIMENSION.ID.likeIgnoreCase("%" + term + "%")
                .or(DIMENSION.DESCRIPTION.likeIgnoreCase("%" + term + "%"));
    }

    @Override
    public Optional<Dimension> find(final String id) {
        return doFind(id)
                .fetchOptionalInto(Dimension.class);
    }

    @Override
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

    @Override
    public void update(final Dimension dimension) {
        db.update(DIMENSION)
                .set(DIMENSION.SCHEMA, dimension.getSchema())
                .set(DIMENSION.RELATION, dimension.getRelation())
                .set(DIMENSION.DESCRIPTION, dimension.getDescription())
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();
    }

    @Override
    public void delete(final Dimension dimension) {
        db.deleteFrom(DIMENSION)
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();
    }

}
