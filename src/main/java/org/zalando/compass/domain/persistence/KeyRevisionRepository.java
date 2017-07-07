package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.library.Pages;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.domain.persistence.model.Tables.KEY_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.library.Enums.translate;

@Repository
public class KeyRevisionRepository {

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
                        translate(key.getRevision().getType(), RevisionType.class),
                        key.getSchema(),
                        key.getDescription())
                .execute();
    }

    public Page<Revision> findPageRevisions(final int limit, @Nullable final Long after) {
        final List<Revision> revisions = db.select(REVISION.fields())
                .select(val(RevisionType.UPDATE).as(KEY_REVISION.REVISION_TYPE)) // TODO HACK!
                .from(REVISION)
                .where(exists(selectOne()
                        .from(KEY_REVISION)
                        .where(KEY_REVISION.REVISION.eq(REVISION.ID))
                        .and(trueCondition())))
                .orderBy(REVISION.ID.desc())
                .seekAfter(firstNonNull(after, Long.MAX_VALUE)) // TODO find a better way
                .limit(limit + 1)
                .fetch().map(this::mapRevision);

        return Pages.page(revisions, limit);
    }

    public Optional<PageRevision<Key>> findPage(final long revision) {
        final Optional<Revision> optional = db.select(REVISION.fields())
                .from(REVISION)
                .where(REVISION.ID.eq(revision))
                .fetchOptionalInto(Revision.class);

        return optional.map(r -> {
            // TODO get rid of fqcn
            final org.zalando.compass.domain.persistence.model.tables.KeyRevision inner = KEY_REVISION.as("inner");
            final List<Key> keys = db.select(KEY_REVISION.fields())
                    .from(KEY_REVISION)
                    .where(KEY_REVISION.REVISION_TYPE.ne(RevisionType.DELETE))
                    .and(KEY_REVISION.REVISION.eq(select(max(inner.REVISION))
                            .from(inner)
                            .where(inner.ID.eq(KEY_REVISION.ID)) // TODO test with multiple keys
                            .and(inner.REVISION.le(revision))))
                    .fetchInto(Key.class);

            return new PageRevision<>(r, keys);
        });
    }

    public Page<Revision> findRevisions(final String id, final int limit, @Nullable final Long after) {
        final List<Revision> revisions = db.select(REVISION.fields())
                .select(KEY_REVISION.REVISION_TYPE)
                .from(REVISION)
                .join(KEY_REVISION).on(KEY_REVISION.REVISION.eq(REVISION.ID))
                .where(KEY_REVISION.ID.eq(id))
                .orderBy(REVISION.ID.desc())
                .seekAfter(firstNonNull(after, Long.MAX_VALUE)) // TODO find a better way
                .limit(limit + 1)
                .fetch().map(this::mapRevision);

        return Pages.page(revisions, limit);
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
                mapRevision(record),
                record.get(KEY_REVISION.SCHEMA),
                record.get(KEY_REVISION.DESCRIPTION)
        );
    }

    private Revision mapRevision(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP),
                translate(record.get(KEY_REVISION.REVISION_TYPE), Revision.Type.class),
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
