package org.zalando.compass.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.model.Key;

import java.net.URI;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class KeyCollectionRevisionRepresentation {

    @JsonProperty("latest-version")
    URI latestVersion;

    @JsonProperty("predecssor-version")
    URI predecessorVersion;

    @JsonProperty("successor-version")
    URI successorVersion;

    // TODO without href
    RevisionRepresentation revision;
    List<KeyRepresentation> keys;

}
