package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.persistence.model.tables.pojos.KeyRow;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.jooq.impl.DSL.selectOne;
import static org.zalando.compass.domain.persistence.model.Tables.KEY;

@Component
public class KeyRepository implements Repository<KeyRow, String, KeyCriteria> {

    private final DSLContext db;

    @Autowired
    public KeyRepository(final DSLContext db) {
        this.db = db;
    }

    @Override
    public boolean create(final KeyRow key) throws IOException {
        try {
            db.insertInto(KEY)
                    .columns(KEY.ID, KEY.SCHEMA, KEY.DESCRIPTION)
                    .values(key.getId(), key.getSchema(), key.getDescription());
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
    public Optional<KeyRow> find(final String id) throws IOException {
        @Nullable final KeyRow row = db.select(KEY.fields())
                .from(KEY)
                .where(KEY.ID.eq(id))
                .fetchOneInto(KeyRow.class);

        return Optional.ofNullable(row);
    }

    @Override
    public List<KeyRow> findAll() {
        return db.select(KEY.fields())
                .from(KEY)
                .orderBy(KEY.ID.asc())
                .fetchInto(KeyRow.class);
    }

    @Override
    public List<KeyRow> findAll(final KeyCriteria criteria) throws IOException {
        final Set<String> keys = criteria.getKeys();

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        return db.select(KEY.fields())
                .from(KEY)
                .where(KEY.ID.in(keys))
                .orderBy(KEY.ID.asc())
                .fetchInto(KeyRow.class);
    }

    @Override
    public boolean update(final KeyRow key) throws IOException {
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
