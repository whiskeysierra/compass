package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.zalando.compass.domain.persistence.model.Tables.KEY_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.library.Enums.translate;

@Repository
public class RevisionRepository {

    private final DSLContext db;

    @Autowired
    public RevisionRepository( final DSLContext db) {
        this.db = db;
    }

    public long create(final Revision revision) {
        return db.insertInto(REVISION)
                .columns(REVISION.TIMESTAMP, REVISION.USER, REVISION.COMMENT)
                .values(revision.getTimestamp(), revision.getUser(), revision.getComment())
                .returning(REVISION.ID)
                .fetchOne().getId();
    }

    public Optional<Revision> read(final long id) {
        return db.select(REVISION.fields())
                .from(REVISION)
                .where(REVISION.ID.eq(id))
                .fetchOptional(this::mapRevision);
    }

    private Revision mapRevision(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP),
                Revision.Type.UPDATE, // TODO hack, doesn't belong here
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
