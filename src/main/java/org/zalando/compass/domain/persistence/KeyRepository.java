package org.zalando.compass.domain.persistence;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Key;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.trueCondition;
import static org.zalando.compass.domain.persistence.model.Tables.KEY;

@Repository
public class KeyRepository {

    private final DSLContext db;

    @Autowired
    public KeyRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final Key key) {
        db.insertInto(KEY)
                .columns(KEY.ID, KEY.SCHEMA, KEY.DESCRIPTION)
                .values(key.getId(), key.getSchema(), key.getDescription())
                .execute();
    }

    public Optional<Key> find(final String id) {
        return doFind(id)
                .fetchOptionalInto(Key.class);
    }

    public Optional<Key> lock(final String id) {
        return doFind(id)
                .forUpdate()
                .fetchOptionalInto(Key.class);
    }

    private SelectConditionStep<Record> doFind(final String id) {
        return db.select()
                .from(KEY)
                .where(KEY.ID.eq(id));
    }

    public List<Key> findAll(@Nullable final String term) {
        return db.select()
                .from(KEY)
                .where(toCondition(term))
                .orderBy(KEY.ID.asc())
                .fetchInto(Key.class);
    }

    private Condition toCondition(@Nullable final String term) {
        if (term == null) {
            return trueCondition();
        }

        return KEY.ID.likeIgnoreCase("%" + term + "%")
                .or(KEY.DESCRIPTION.likeIgnoreCase("%" + term + "%"));
    }

    public void update(final Key key) {
        db.update(KEY)
                .set(KEY.SCHEMA, key.getSchema())
                .set(KEY.DESCRIPTION, key.getDescription())
                .where(KEY.ID.eq(key.getId()))
                .execute();
    }

    public void delete(final String key) {
        db.deleteFrom(KEY)
                .where(KEY.ID.eq(key))
                .execute();
    }

}
