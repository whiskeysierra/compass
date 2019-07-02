package org.zalando.compass.core.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Value;

import static lombok.AccessLevel.PRIVATE;
import static org.zalando.compass.library.Maps.transform;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class ValueRepresentation {

    @Wither // TODO empty, non-null map as default value would be preferable
    ImmutableMap<String, JsonNode> dimensions;
    JsonNode value;

    public static ValueRepresentation valueOf(final Value value) {
        return new ValueRepresentation(transform(value.getDimensions(), Dimension::getId), value.getValue());
    }

}
