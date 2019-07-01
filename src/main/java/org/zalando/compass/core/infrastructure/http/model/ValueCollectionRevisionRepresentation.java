package org.zalando.compass.core.infrastructure.http.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class ValueCollectionRevisionRepresentation {

    RevisionRepresentation revision;
    List<ValueRepresentation> values;

}
