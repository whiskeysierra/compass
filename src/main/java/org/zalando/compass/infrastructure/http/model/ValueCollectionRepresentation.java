package org.zalando.compass.infrastructure.http.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public final class ValueCollectionRepresentation {

    List<ValueRepresentation> values;

}
