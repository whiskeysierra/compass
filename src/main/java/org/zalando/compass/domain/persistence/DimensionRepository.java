package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION;

@Component
public class DimensionRepository {

    private final DSLContext db;

    @Autowired
    public DimensionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final Dimension dimension) {
        db.insertInto(DIMENSION)
                .columns(DIMENSION.ID, DIMENSION.SCHEMA, DIMENSION.RELATION, DIMENSION.DESCRIPTION)
                .values(dimension.getId(), dimension.getSchema(), dimension.getRelation(),
                        dimension.getDescription())
                .execute();
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

    public List<Dimension> findAll(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        return doFindAll(dimensions)
                .fetchInto(Dimension.class);
    }

    public List<Dimension> lockAll(final Set<String> dimensions) {
        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        return doFindAll(dimensions)
                .forUpdate()
                .fetchInto(Dimension.class);
    }

    private SelectSeekStep1<Record, String> doFindAll(final Set<String> dimensions) {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(DIMENSION.ID.in(dimensions))
                .orderBy(DIMENSION.ID.asc());
    }

    public List<Dimension> findAll() {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .orderBy(DIMENSION.ID)
                .fetchInto(Dimension.class);
    }

    public void update(final Dimension dimension) {
        db.update(DIMENSION)
                .set(DIMENSION.SCHEMA, dimension.getSchema())
                .set(DIMENSION.RELATION, dimension.getRelation())
                .set(DIMENSION.DESCRIPTION, dimension.getDescription())
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();
    }

    public boolean delete(final String id) {
        final int deletes = db.deleteFrom(DIMENSION)
                .where(DIMENSION.ID.eq(id))
                .execute();

        return deletes == 1;
    }

}
