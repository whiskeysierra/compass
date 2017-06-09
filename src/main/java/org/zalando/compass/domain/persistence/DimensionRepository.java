package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        final int inserts = db.insertInto(DIMENSION)
                .columns(DIMENSION.ID, DIMENSION.SCHEMA, DIMENSION.RELATION, DIMENSION.DESCRIPTION)
                .values(dimension.getId(), dimension.getSchema(), dimension.getRelation(),
                        dimension.getDescription())
                .onDuplicateKeyIgnore()
                .execute();

        return inserts == 1;
    }

    @Override
    public Optional<Dimension> find(final String id) {
        return db.select()
                .from(DIMENSION)
                .where(DIMENSION.ID.eq(id))
                .fetchOptionalInto(Dimension.class);
    }

    public Optional<Dimension> lock(final String id) {
        return db.select()
                .from(DIMENSION)
                .where(DIMENSION.ID.eq(id))
                .forUpdate()
                .fetchOptionalInto(Dimension.class);
    }

    @Override
    public List<Dimension> findAll(final DimensionCriteria criteria) {
        final Set<String> dimensions = criteria.getDimensions();

        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(DIMENSION.ID.in(dimensions))
                .orderBy(DIMENSION.ID)
                .fetchInto(Dimension.class);
    }

    @Override
    public List<Dimension> findAll() {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .orderBy(DIMENSION.ID)
                .fetchInto(Dimension.class);
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
    public void delete(final String id) {
        final int deletes = db.deleteFrom(DIMENSION)
                .where(DIMENSION.ID.eq(id))
                .execute();

        if (deletes == 0) {
            throw new NotFoundException();
        }
    }

}
