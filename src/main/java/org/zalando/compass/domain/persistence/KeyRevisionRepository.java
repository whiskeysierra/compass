package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;
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

    public Page<KeyRevision> findAll(final String id, final int limit, @Nullable final Long after) {
        final List<KeyRevision> revisions = db.select(KEY_REVISION.fields())
                .select(REVISION.fields())
                .from(KEY_REVISION)
                .join(REVISION).on(REVISION.ID.eq(KEY_REVISION.REVISION))
                .where(KEY_REVISION.ID.eq(id))
                .orderBy(REVISION.ID.desc())
                .seekAfter(firstNonNull(after, Long.MAX_VALUE)) // TODO find a better way
                .limit(limit + 1)
                .fetch().map(this::mapRevision);

        // TODO library
        if (revisions.size() > limit) {
            final List<KeyRevision> items = revisions.subList(0, limit);
            final KeyRevision next = items.get(items.size() - 1);
            return new Page<>(items, next);
        } else {
            return new Page<>(revisions, null);
        }
    }

    private KeyRevision mapRevision(final Record record) {
        return new KeyRevision(
                record.get(KEY_REVISION.ID),
                new Revision(
                        record.get(REVISION.ID),
                        record.get(REVISION.TIMESTAMP),
                        translate(record.get(KEY_REVISION.REVISION_TYPE), Revision.Type.class),
                        record.get(REVISION.USER),
                        record.get(REVISION.COMMENT)
                ),
                record.get(KEY_REVISION.SCHEMA),
                record.get(KEY_REVISION.DESCRIPTION)
        );
    }

}
