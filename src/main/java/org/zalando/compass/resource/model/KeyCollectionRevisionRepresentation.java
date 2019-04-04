package org.zalando.compass.resource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.net.URI;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class KeyCollectionRevisionRepresentation {

    RevisionRepresentation revision;
    URI next;
    URI prev;
    List<KeyRepresentation> keys;

}
