package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
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
                .values(timestamp.toLocalDateTime(), user, comment)
                .returning(REVISION.ID)
                .fetchOne().getId();
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
