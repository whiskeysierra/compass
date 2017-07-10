package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;

@Repository
public class RevisionRepository {

    private final DSLContext db;

    @Autowired
    public RevisionRepository( final DSLContext db) {
        this.db = db;
    }

    public long create(final OffsetDateTime timestamp, final String user, @Nullable final String comment) {
        return db.insertInto(REVISION)
                .columns(REVISION.TIMESTAMP, REVISION.USER, REVISION.COMMENT)
                .values(toUTC(timestamp), user, comment)
                .returning(REVISION.ID)
                .fetchOne().getId();
    }

    // TODO why do we need this? The clock should be UTC already...
    private LocalDateTime toUTC(final OffsetDateTime timestamp) {
        return timestamp.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public Optional<Revision> read(final long id) {
        return db.select(REVISION.fields())
                .from(REVISION)
                .where(REVISION.ID.eq(id))
                .fetchOptional(this::mapRevisionWithoutType);
    }

    private Revision mapRevisionWithoutType(final Record record) {
        return new Revision(
                record.get(REVISION.ID),
                record.get(REVISION.TIMESTAMP).atOffset(UTC),
                null,
                record.get(REVISION.USER),
                record.get(REVISION.COMMENT)
        );
    }

}
