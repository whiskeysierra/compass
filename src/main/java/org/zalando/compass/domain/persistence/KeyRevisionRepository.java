package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.trueCondition;
import static org.zalando.compass.domain.persistence.model.Tables.KEY_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;

@Repository
public class KeyRevisionRepository {

    private static final org.zalando.compass.domain.persistence.model.tables.KeyRevision SELF = KEY_REVISION.as("self");

    private final DSLContext db;

    @Autowired
    public KeyRevisionRepository(final DSLContext db) {
        this.db = db;
    }

    public void create(final KeyRevision key) {
        db.insertInto(KEY_REVISION)
                .columns(KEY_REVISION.ID,
                        KEY_REVISION.REVISION,
                        KEY_REVISION.REVISION_TYPE,
                        KEY_REVISION.SCHEMA,
                        KEY_REVISION.DESCRIPTION)
                .values(key.getId(),
                        key.getRevision().getId(),
                        key.getRevision().getType(),
                        key.getSchema(),
                        key.getDescription())
                .execute();
    }

    public List<Revision> findPageRevisions(final Pagination<Long> query) {
        return query.seek(db.select(REVISION.fields())
                .from(REVISION)
                .where(exists(selectOne()
                        .from(KEY_REVISION)
                        .where(KEY_REVISION.REVISION.eq(REVISION.ID))
                        .and(trueCondition()))), REVISION.ID, SortOrder.DESC)
                .fetch().map(this::mapRevisionWithoutType);
    }

    public List<Key> findPage(final long revisionId, final Pagination<String> query) {
        return query.seek(db.select(asList(
                KEY_REVISION.ID,
                KEY_REVISION.SCHEMA,
                KEY_REVISION.DESCRIPTION))
                .from(KEY_REVISION)
                .where(KEY_REVISION.REVISION_TYPE.ne(RevisionType.DELETE))
                .and(KEY_REVISION.REVISION.eq(select(max(SELF.REVISION))
                        .from(SELF)
                        .where(SELF.ID.eq(KEY_REVISION.ID))
                        .and(SELF.REVISION.le(revisionId)))), KEY_REVISION.ID, SortOrder.ASC)
                .fetchInto(Key.class);
    }

    public List<Revision> findRevisions(final String id, final Pagination<Long> query) {
        return query.seek(db.select(REVISION.fields())
                .select(KEY_REVISION.REVISION_TYPE)
                .from(REVISION)
                .join(KEY_REVISION).on(KEY_REVISION.REVISION.eq(REVISION.ID))
                .where(KEY_REVISION.ID.eq(id)), REVISION.ID, SortOrder.DESC)
                .fetch().map(this::mapRevisionWithType);
    }

    public Optional<KeyRevision> find(final String id, final long revision) {
        return db.select(KEY_REVISION.fields())
                .select(REVISION.fields())
                .from(KEY_REVISION)
                .join(REVISION).on(REVISION.ID.eq(KEY_REVISION.REVISION))
                .where(KEY_REVISION.ID.eq(id))
                .and(REVISION.ID.eq(revision))
                .fetchOptional()
                .map(this::mapKey);
    }

    private KeyRevision mapKey(final Record record) {
        return new KeyRevision(
                record.get(KEY_REVISION.ID),
                mapRevisionWithType(record),
                record.get(KEY_REVISION.SCHEMA),
                record.get(KEY_REVISION.DESCRIPTION)
        );
    }

    private Revision mapRevisionWithType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).withOffsetSameInstant(UTC),
                record.get(KEY_REVISION.REVISION_TYPE),
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

    private Revision mapRevisionWithoutType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).withOffsetSameInstant(UTC),
                null,
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
