package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.model.Revision;

import java.net.URI;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public class RevisionRepresentation {

    Long id;
    LocalDateTime timestamp;
    URI href;
    Revision.Type type;
    String user;
    String comment;

}
