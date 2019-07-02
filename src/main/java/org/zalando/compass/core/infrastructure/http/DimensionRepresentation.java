package org.zalando.compass.core.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.kernel.domain.model.Dimension;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class DimensionRepresentation {

    String id;
    JsonNode schema;
    String relation;
    String description;

    public static DimensionRepresentation valueOf(final Dimension dimension) {
        return new DimensionRepresentation(
                dimension.getId(),
                dimension.getSchema(),
                dimension.getRelation(),
                dimension.getDescription());
    }

}
