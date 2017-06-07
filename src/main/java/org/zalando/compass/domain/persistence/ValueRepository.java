package org.zalando.compass.domain.persistence;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Realization;
import org.zalando.compass.domain.persistence.model.tables.pojos.ValueRow;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.function;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.domain.persistence.ValueCriteria.withoutCriteria;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE;

@Component
public class ValueRepository implements Repository<ValueRow, Realization, ValueCriteria> {

    private final DSLContext db;

    @Autowired
    public ValueRepository(final DSLContext db) {
        this.db = db;
    }

    @Override
    public boolean create(final ValueRow value) throws IOException {
        final int inserts = db.insertInto(VALUE)
                .columns(VALUE.KEY, VALUE.DIMENSIONS, VALUE.VALUE_)
                .values(value.getKey(), value.getDimensions(), value.getValue())
                .execute();

        return inserts > 0;
    }

    @Override
    public Optional<ValueRow> find(final Realization id) throws IOException {
        @Nullable final ValueRow row = db.select(VALUE.fields())
                .from(VALUE)
                .where(VALUE.KEY.eq(id.getKey()))
                .and(VALUE.DIMENSIONS.isNotNull()) // TODO match on dimensions somehow?!
                .fetchOneInto(ValueRow.class);

        return Optional.ofNullable(row);
    }

    @Override
    public List<ValueRow> findAll() throws IOException {
        return findAll(withoutCriteria());
    }

    @Override
    public List<ValueRow> findAll(final ValueCriteria criteria) throws IOException {
        return db.select(VALUE.fields())
                .from(VALUE)
                .where(toCondition(criteria))
                .fetchInto(ValueRow.class);
    }

    private Condition toCondition(final ValueCriteria criteria) {
        if (criteria.getKey() != null) {
            return VALUE.KEY.eq(criteria.getKey());
        } else if (criteria.getKeyPattern() != null) {
            return VALUE.KEY.likeIgnoreCase(criteria.getKeyPattern());
        } else if (criteria.getDimension() != null) {
            return function("JSONB_EXISTS", Boolean.class, VALUE.DIMENSIONS, val(criteria.getDimension())).isTrue();
        } else {
            return trueCondition();
        }
    }

    @Override
    public boolean update(final ValueRow value) throws IOException {
        final int updates = db.update(VALUE)
                .set(VALUE.VALUE_, value.getValue())
                .where(VALUE.KEY.eq(value.getKey()))
                .and(VALUE.DIMENSIONS.eq(value.getDimensions()))
                .execute();

        return updates > 0;
    }

    @Override
    public void delete(final Realization id) throws IOException {
        /*
         * TODO optimize query
         *
         * SELECT COALESCE(JSONB_OBJECT_AGG(key, value), '{}'::JSONB)
         *   FROM JSONB_EACH_TEXT(dimensions)) = :dimensions::JSONB
         *
         */

        // TODO find values and delete all of them in one query

        final int deletions = 0;

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}
