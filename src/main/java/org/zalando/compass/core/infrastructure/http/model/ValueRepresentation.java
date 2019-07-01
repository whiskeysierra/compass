package org.zalando.compass.core.infrastructure.http.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import org.zalando.compass.kernel.domain.model.Value;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class ValueRepresentation {

    @Wither // TODO empty, non-null map as default value would be preferable
    ImmutableMap<String, JsonNode> dimensions;
    JsonNode value;

    public static ValueRepresentation valueOf(final Value value) {
        return new ValueRepresentation(value.getDimensions(), value.getValue());
    }

    public Value toValue(final long index) {
        return new Value(dimensions, index, value);
    }

}
