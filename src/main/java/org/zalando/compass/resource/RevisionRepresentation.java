package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.persistence.model.enums.RevisionType;
import org.zalando.compass.library.LowerCaseConverter;

import java.net.URI;
import java.time.OffsetDateTime;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class RevisionRepresentation {

    Long id;
    OffsetDateTime timestamp;
    URI href;

    @JsonSerialize(converter = LowerCaseConverter.class)
    RevisionType type;
    String user;
    String comment;

}
