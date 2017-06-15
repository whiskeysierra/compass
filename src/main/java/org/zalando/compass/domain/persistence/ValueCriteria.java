package org.zalando.compass.domain.persistence;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class ValueCriteria {

    String key;
    String dimension;

    public static ValueCriteria byKey(final String key) {
        return new ValueCriteria(key, null);
    }

    public static ValueCriteria byDimension(final String dimension) {
        return new ValueCriteria(null, dimension);
    }

}
