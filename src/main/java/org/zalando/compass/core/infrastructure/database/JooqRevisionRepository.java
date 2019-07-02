package org.zalando.compass.core.infrastructure.database;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.spi.repository.RevisionRepository;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.zalando.compass.core.infrastructure.database.model.Tables.REVISION;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
class JooqRevisionRepository implements RevisionRepository {

    private final DSLContext db;

    @Override
    public long create(final OffsetDateTime timestamp, final String user, @Nullable final String comment) {
        return db.insertInto(REVISION)
                .columns(REVISION.TIMESTAMP, REVISION.USER, REVISION.COMMENT)
                .values(timestamp, user, comment)
                .returning(REVISION.ID)
                .fetchOne().getId();
    }

    @Override
    public Optional<Revision> read(final long id) {
        return db.select(REVISION.fields())
                .from(REVISION)
                .where(REVISION.ID.eq(id))
                .fetchOptional(this::mapRevisionWithoutType);
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
