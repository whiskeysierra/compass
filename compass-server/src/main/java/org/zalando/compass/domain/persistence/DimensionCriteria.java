package org.zalando.compass.domain.persistence;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class DimensionCriteria {

    private Set<String> dimensions;

    public static DimensionCriteria dimensions(final Set<String> dimensions) {
        return new DimensionCriteria(dimensions);
    }

}
