package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.persistence.model.tables.pojos.KeyRow;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
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
    public boolean create(final Key key) {
        try {
            db.insertInto(KEY)
                    .columns(KEY.ID, KEY.SCHEMA, KEY.DESCRIPTION)
                    .values(key.getId(), key.getSchema(), key.getDescription())
                    .execute();
            return true;
        } catch (final DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public boolean exists(final String key) {
        return db.fetchExists(
                selectOne()
                .from(KEY)
                .where(KEY.ID.eq(key)));
    }

    @Override
    public Optional<Key> find(final String id) {
        @Nullable final KeyRow row = db.select()
                .from(KEY)
                .where(KEY.ID.eq(id))
                .fetchOneInto(KeyRow.class);

        return Optional.ofNullable(row).map(this::map);
    }

    @Override
    public List<Key> findAll() {
        return db.select()
                .from(KEY)
                .orderBy(KEY.ID.asc())
                .fetchInto(KeyRow.class)
                .stream()
                .map(this::map)
                .collect(toList());
    }

    @Override
    public List<Key> findAll(final KeyCriteria criteria) {
        final Set<String> keys = criteria.getKeys();

        if (keys.isEmpty()) {
            // TODO is this actually correct?!
            return Collections.emptyList();
        }

        return db.select()
                .from(KEY)
                .where(KEY.ID.in(keys))
                .orderBy(KEY.ID.asc())
                .fetchInto(KeyRow.class)
                .stream()
                .map(this::map)
                .collect(toList());
    }

    private Key map(final KeyRow row) {
        return new Key(row.getId(), row.getSchema(), row.getDescription());
    }

    @Override
    public boolean update(final Key key) {
        final int updates = db.update(KEY)
                .set(KEY.SCHEMA, key.getSchema())
                .set(KEY.DESCRIPTION, key.getDescription())
                .where(KEY.ID.eq(key.getId()))
                .execute();

        return updates > 0;
    }

    @Override
    public void delete(final String key) {
        final int deletions = db.deleteFrom(KEY)
                .where(KEY.ID.eq(key))
                .execute();

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}
