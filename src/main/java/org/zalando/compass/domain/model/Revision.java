package org.zalando.compass.domain.model;

import lombok.experimental.Wither;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;

import java.time.LocalDateTime;

@lombok.Value
public final class Revision {

    // TODO long?
    @Wither
    Long id;

    LocalDateTime timestamp;

    @Wither
    RevisionType type;

    String user;
    String comment;

    public Revision withTypeUpdate() {
        return withType(RevisionType.UPDATE);
    }

}
