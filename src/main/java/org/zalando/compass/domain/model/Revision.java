package org.zalando.compass.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.Wither;

import java.time.LocalDateTime;

@lombok.Value
public final class Revision {

    public enum Type {

        CREATE, UPDATE, DELETE;

        @JsonValue // TODO move this closer to the web layer
        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    // TODO long?
    @Wither
    Long id;

    LocalDateTime timestamp;

    @Wither
    Type type;

    String user;
    String comment;

}
