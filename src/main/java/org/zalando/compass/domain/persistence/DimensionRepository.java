package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.persistence.model.Tables;
import org.zalando.compass.domain.persistence.model.tables.daos.DimensionDao;
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

@Component
public class DimensionRepository implements Repository<DimensionRow, String, DimensionCriteria> {

    private final DimensionDao dao;
    private final DSLContext context;

    @Autowired
    public DimensionRepository(final DimensionDao dao, final DSLContext jooq) {
        this.dao = dao;
        this.context = jooq;
    }

    @Override
    public boolean create(final DimensionRow dimension) throws IOException {
        try {
            dao.insert(new DimensionRow(
                    dimension.getId(),
                    null,
                    dimension.getSchema(),
                    dimension.getRelation(),
                    dimension.getDescription()
            ));
            return true;
        } catch (final DuplicateKeyException e) {
            // TODO verify that this actually works
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
            return Collections.emptyList();
        }

        return context.select(Tables.DIMENSION.fields())
                .from(Tables.DIMENSION)
                .where(Tables.DIMENSION.ID.in(dimensions))
                .orderBy(Tables.DIMENSION.PRIORITY.asc())
                .fetchInto(DimensionRow.class);
    }

    @Override
    public List<DimensionRow> findAll() {
        return context.select(Tables.DIMENSION.fields())
                .from(Tables.DIMENSION)
                .orderBy(Tables.DIMENSION.PRIORITY.asc())
                .fetchInto(DimensionRow.class);
    }

    @Override
    public boolean update(final DimensionRow dimension) throws IOException {
        final int updates = context.update(Tables.DIMENSION)
                .set(Tables.DIMENSION.SCHEMA, dimension.getSchema())
                .set(Tables.DIMENSION.RELATION, dimension.getRelation())
                .set(Tables.DIMENSION.DESCRIPTION, dimension.getDescription())
                .where(Tables.DIMENSION.ID.eq(dimension.getId()))
                .execute();

        return updates > 0;
    }

    public void reorder(final List<String> dimensions) {
        final List<Row2<String, Integer>> ranks = new ArrayList<>(dimensions.size());
        final ListIterator<String> iterator = dimensions.listIterator();

        while (iterator.hasNext()) {
            ranks.add(row(iterator.next(), iterator.nextIndex()));
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        final Row2<String, Integer>[] rows = ranks.toArray(new Row2[ranks.size()]);
        final Table<Record2<String, Integer>> values = values(rows);

        context.update(Tables.DIMENSION)
                .set(Tables.DIMENSION.PRIORITY, field("new_priority", Integer.class))
                .from(values.as("ranks", "dimension_id", "new_priority"))
                .where(Tables.DIMENSION.ID.eq(field("dimension_id", String.class)))
                .execute();
    }

    @Override
    public void delete(final String dimension) {
        final int deletions = context.delete(Tables.DIMENSION)
                .where(Tables.DIMENSION.ID.eq(dimension))
                .execute();

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}
