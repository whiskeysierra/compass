package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.model.tables.pojos.DimensionRow;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.zalando.compass.domain.persistence.DimensionCriteria.dimensions;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION;

@Component
public class DimensionRepository implements Repository<Dimension, String, DimensionCriteria> {

    private final DSLContext db;

    @Autowired
    public DimensionRepository(final DSLContext db) {
        this.db = db;
    }

    @Override
    public boolean create(final Dimension dimension) {
        try {
            db.insertInto(DIMENSION)
                    .columns(DIMENSION.ID, DIMENSION.SCHEMA, DIMENSION.RELATION, DIMENSION.DESCRIPTION)
                    .values(dimension.getId(), dimension.getSchema(), dimension.getRelation(), dimension.getDescription())
                    .execute();
            return true;
        } catch (final DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public Optional<Dimension> find(final String id) {
        final List<Dimension> dimensions = findAll(dimensions(singleton(id)));
        @Nullable final Dimension dimension = singleResult(dimensions);
        return Optional.ofNullable(dimension);
    }

    @Override
    public List<Dimension> findAll(final DimensionCriteria criteria) {
        final Set<String> dimensions = criteria.getDimensions();

        if (dimensions.isEmpty()) {
            // TODO is that correct?!
            return Collections.emptyList();
        }

        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(DIMENSION.ID.in(dimensions))
                .orderBy(DIMENSION.ID)
                .fetchInto(DimensionRow.class)
                .stream()
                .map(this::map)
                .collect(toList());
    }

    @Override
    public List<Dimension> findAll() {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .orderBy(DIMENSION.ID)
                .fetchInto(DimensionRow.class)
                .stream()
                .map(this::map)
                .collect(toList());
    }

    private Dimension map(final DimensionRow row) {
        return new Dimension(row.getId(), row.getSchema(), row.getRelation(), row.getDescription());
    }

    @Override
    public boolean update(final Dimension dimension) {
        final int updates = db.update(DIMENSION)
                .set(DIMENSION.SCHEMA, dimension.getSchema())
                .set(DIMENSION.RELATION, dimension.getRelation())
                .set(DIMENSION.DESCRIPTION, dimension.getDescription())
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();

        return updates > 0;
    }

    @Override
    public void delete(final String dimension) {
        final int deletions = db.deleteFrom(DIMENSION)
                .where(DIMENSION.ID.eq(dimension))
                .execute();

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}
