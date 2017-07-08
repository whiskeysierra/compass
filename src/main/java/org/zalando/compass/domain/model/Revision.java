package org.zalando.compass.domain.model;

import lombok.experimental.Wither;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@lombok.Value
public final class Revision {

    // TODO long?
    @Wither
    Long id;

    OffsetDateTime timestamp;

    @Wither
    RevisionType type;

    String user;
    String comment;

    public Revision withTypeUpdate() {
        return withType(RevisionType.UPDATE);
    }

}
