package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.zalando.compass.domain.persistence.model.Tables.REVISION;

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
                .values(toUTC(revision.getTimestamp()), revision.getUser(), revision.getComment())
                .returning(REVISION.ID)
                .fetchOne().getId();
    }

    // TODO why do we need this? The clock should be UTC already...
    private LocalDateTime toUTC(final OffsetDateTime timestamp) {
        return timestamp.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

}
