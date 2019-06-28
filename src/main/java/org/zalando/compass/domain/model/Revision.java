package org.zalando.compass.domain.model;

import lombok.experimental.Wither;
import org.zalando.compass.infrastructure.database.model.enums.RevisionType;

import java.time.OffsetDateTime;

@lombok.Value
public final class Revision {

    long id;

    OffsetDateTime timestamp;

    @Wither
    RevisionType type;

    String user;
    String comment;

    public Revision withTypeUpdate() {
        return withType(RevisionType.UPDATE);
    }

}
