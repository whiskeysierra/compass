package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.jooq.impl.DSL.selectOne;
import static org.zalando.compass.domain.persistence.model.Tables.KEY;

@Component
public class KeyRepository implements Repository<Key, String, KeyCriteria> {

    private final DSLContext db;

    @Autowired
    public KeyRepository(final DSLContext db) {
        this.db = db;
    }

    @Override
    public void create(final Key key) {
        db.insertInto(KEY)
                .columns(KEY.ID, KEY.SCHEMA, KEY.DESCRIPTION)
                .values(key.getId(), key.getSchema(), key.getDescription())
                .execute();
    }

    @Override
    public Optional<Key> find(final String id) {
        return doFind(id)
                .fetchOptionalInto(Key.class);
    }

    @Override
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

    @Override
    public List<Key> findAll() {
        return db.select()
                .from(KEY)
                .orderBy(KEY.ID.asc())
                .fetchInto(Key.class);
    }

    @Override
    public List<Key> findAll(final KeyCriteria criteria) {
        final Set<String> keys = criteria.getKeys();

        if (keys.isEmpty()) {
            // TODO is this actually correct?!
            return Collections.emptyList();
        }

        return doFindAll(keys)
                .fetchInto(Key.class);
    }

    @Override
    public List<Key> lockAll(final KeyCriteria criteria) {
        final Set<String> keys = criteria.getKeys();

        // TODO share this with findAll
        if (keys.isEmpty()) {
            // TODO is this actually correct?!
            return Collections.emptyList();
        }

        return doFindAll(keys)
                .forUpdate()
                .fetchInto(Key.class);
    }

    private SelectSeekStep1<Record, String> doFindAll(final Set<String> keys) {
        return db.select()
                .from(KEY)
                .where(KEY.ID.in(keys))
                .orderBy(KEY.ID.asc());
    }

    @Override
    public void update(final Key key) {
        db.update(KEY)
                .set(KEY.SCHEMA, key.getSchema())
                .set(KEY.DESCRIPTION, key.getDescription())
                .where(KEY.ID.eq(key.getId()))
                .execute();
    }

    @Override
    public void delete(final String key) {
        final int deletes = db.deleteFrom(KEY)
                .where(KEY.ID.eq(key))
                .execute();

        if (deletes == 0) {
            // TODO not here? needed at all?
            throw new NotFoundException();
        }
    }

}
