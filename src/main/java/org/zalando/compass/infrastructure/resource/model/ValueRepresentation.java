package org.zalando.compass.infrastructure.resource.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import org.zalando.compass.domain.model.Value;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class ValueRepresentation {

    @Wither
    ImmutableMap<String, JsonNode> dimensions;
    JsonNode value;

    public static ValueRepresentation valueOf(final Value value) {
        return new ValueRepresentation(value.getDimensions(), value.getValue());
    }

    public Value toValue(final long index) {
        return new Value(dimensions, index, value);
    }

}
