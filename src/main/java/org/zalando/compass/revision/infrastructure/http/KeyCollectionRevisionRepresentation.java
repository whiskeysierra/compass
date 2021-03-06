package org.zalando.compass.revision.infrastructure.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.core.infrastructure.http.KeyRepresentation;
import org.zalando.compass.core.infrastructure.http.RevisionRepresentation;

import java.net.URI;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class KeyCollectionRevisionRepresentation {

    RevisionRepresentation revision;
    URI next;
    URI prev;
    List<KeyRepresentation> keys;

}
