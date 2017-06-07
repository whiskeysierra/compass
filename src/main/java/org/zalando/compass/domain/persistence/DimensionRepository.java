package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.persistence.model.tables.pojos.DimensionRow;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.values;
import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.zalando.compass.domain.persistence.DimensionCriteria.dimensions;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION;

@Component
public class DimensionRepository implements Repository<DimensionRow, String, DimensionCriteria> {

    private final DSLContext db;

    @Autowired
    public DimensionRepository(final DSLContext db) {
        this.db = db;
    }

    @Override
    public boolean create(final DimensionRow dimension) throws IOException {
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
    public Optional<DimensionRow> find(final String id) throws IOException {
        final List<DimensionRow> dimensions = findAll(dimensions(singleton(id)));
        @Nullable final DimensionRow dimension = singleResult(dimensions);
        return Optional.ofNullable(dimension);
    }

    @Override
    public List<DimensionRow> findAll(final DimensionCriteria criteria) throws IOException {
        final Set<String> dimensions = criteria.getDimensions();

        if (dimensions.isEmpty()) {
            // TODO is that correct?!
            return Collections.emptyList();
        }

        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .where(DIMENSION.ID.in(dimensions))
                .orderBy(DIMENSION.PRIORITY.asc())
                .fetchInto(DimensionRow.class);
    }

    @Override
    public List<DimensionRow> findAll() {
        return db.select(DIMENSION.fields())
                .from(DIMENSION)
                .orderBy(DIMENSION.PRIORITY.asc())
                .fetchInto(DimensionRow.class);
    }

    @Override
    public boolean update(final DimensionRow dimension) throws IOException {
        final int updates = db.update(DIMENSION)
                .set(DIMENSION.SCHEMA, dimension.getSchema())
                .set(DIMENSION.RELATION, dimension.getRelation())
                .set(DIMENSION.DESCRIPTION, dimension.getDescription())
                .where(DIMENSION.ID.eq(dimension.getId()))
                .execute();

        return updates > 0;
    }

    public void reorder(final List<String> dimensions) {
        db.update(DIMENSION)
                .set(DIMENSION.PRIORITY, field("ranks.priority", Integer.class))
                .from(index(dimensions).as("ranks", "dimension", "priority"))
                .where(DIMENSION.ID.eq(field("ranks.dimension", String.class)))
                .execute();
    }

    private static <T> Table<Record2<T, Integer>> index(final List<T> dimensions) {
        final List<Row2<T, Integer>> ranks = new ArrayList<>(dimensions.size());
        final ListIterator<T> iterator = dimensions.listIterator();

        while (iterator.hasNext()) {
            ranks.add(row(iterator.next(), iterator.nextIndex()));
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        final Row2<T, Integer>[] rows = ranks.toArray(new Row2[ranks.size()]);

        return values(rows);
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
