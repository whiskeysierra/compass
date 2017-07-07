package org.zalando.compass.domain.persistence;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;

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
                .values(revision.getTimestamp(), revision.getUser(), revision.getComment())
                .returning(REVISION.ID)
                .fetchOne().getId();
    }

}
