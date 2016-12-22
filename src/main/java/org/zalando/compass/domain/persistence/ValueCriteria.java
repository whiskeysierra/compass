package org.zalando.compass.domain.persistence;

import lombok.AllArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class ValueCriteria {

    private String key;
    private String keyPattern;
    private String dimension;

    public static ValueCriteria byKey(final String key) {
        return new ValueCriteria(key, null, null);

    }

    public static ValueCriteria byKeyPattern(final String keyPattern) {
        return new ValueCriteria(null, keyPattern, null);
    }

    public static ValueCriteria byDimension(final String dimension) {
        return new ValueCriteria(null, null, dimension);
    }

    public static ValueCriteria withoutCriteria() {
        return new ValueCriteria(null, null, null);
    }

}
