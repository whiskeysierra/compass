package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import org.zalando.compass.domain.model.Revision;

import java.net.URI;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public class RevisionRepresentation {

    @Wither
    Long id;

    LocalDateTime timestamp;

    URI href;

    @Wither
    Revision.Type type;

    String user;
    String comment;

}
