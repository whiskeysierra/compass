package org.zalando.compass.core.infrastructure.database;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.core.domain.spi.repository.RelationRepository;
import org.zalando.compass.core.domain.spi.repository.lock.DimensionLockRepository;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.jooq.impl.DSL.trueCondition;
import static org.zalando.compass.core.infrastructure.database.model.Tables.DIMENSION;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
class JooqDimensionRepository implements DimensionRepository, DimensionLockRepository {

    private final DSLContext db;
    private final RelationRepository repository;

    @Override
    public void create(final Dimension dimension) {
        db.insertInto(DIMENSION)
                .columns(DIMENSION.ID, DIMENSION.SCHEMA, DIMENSION.RELATION, DIMENSION.DESCRIPTION)
                .values(dimension.getId(), dimension.getSchema(), dimension.getRelation().getId(),
                        dimension.getDescription())
                .execute();
    }

    @Override
    public Set<Dimension> lockAll(final Set<Dimension> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptySet();
        }

        // TODO can we get rid of this?
        return ImmutableSet.copyOf(doFindAll(dimensions.stream().map(Dimension::getId).collect(toSet()))
                .forUpdate()
                .fetch(this::map));
    }

    @Override
    public Set<Dimension> findAll(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptySet();
        }

        // TODO can we get rid of this?
        return ImmutableSet.copyOf(doFindAll(dimensions)
                .fetch(this::map));
    }

    // TODO this won't fetch the relation properly
    private SelectSeekStep1<Record, String> doFindAll(final Set<String> dimensions) {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(DIMENSION.ID.in(dimensions))
                .orderBy(DIMENSION.ID.asc());
    }

    @Override
    public Set<Dimension> findAll(@Nullable final String term, final Pagination<String> query) {
        // TODO can we get rid of this?
        return ImmutableSet.copyOf(query.seek(db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(toCondition(term)), DIMENSION.ID, SortOrder.ASC)
                .fetch(this::map));
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
                .fetchOptional(this::map);
    }

    @Override
    public Optional<Dimension> lock(final Dimension dimension) {
        return doFind(dimension.getId())
                .forUpdate()
                .fetchOptional(this::map);
    }

    private Dimension map(final Record record) {
        return new Dimension(
                record.get(DIMENSION.ID),
                record.get(DIMENSION.SCHEMA),
                repository.find(record.get(DIMENSION.RELATION)).orElseThrow(NotFoundException::new),
                record.get(DIMENSION.DESCRIPTION)
        );
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
                .set(DIMENSION.RELATION, dimension.getRelation().getId())
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
