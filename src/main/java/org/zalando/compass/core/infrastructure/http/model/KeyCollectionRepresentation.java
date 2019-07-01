package org.zalando.compass.core.infrastructure.http.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.net.URI;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class KeyCollectionRepresentation {

    URI next;
    URI prev;
    List<KeyRepresentation> keys;

}
