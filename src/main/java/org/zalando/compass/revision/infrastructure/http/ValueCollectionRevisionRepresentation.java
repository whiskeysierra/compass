package org.zalando.compass.revision.infrastructure.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.core.infrastructure.http.RevisionRepresentation;
import org.zalando.compass.core.infrastructure.http.ValueRepresentation;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class ValueCollectionRevisionRepresentation {

    RevisionRepresentation revision;
    List<ValueRepresentation> values;

}
