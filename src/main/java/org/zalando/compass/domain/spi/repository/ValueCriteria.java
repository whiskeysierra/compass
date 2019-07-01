package org.zalando.compass.domain.spi.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
public class ValueCriteria {

    private final String key;
    private final String dimension;

    public static ValueCriteria byKey(final String key) {
        return new ValueCriteria(key, null);
    }

    public static ValueCriteria byDimension(final String dimension) {
        return new ValueCriteria(null, dimension);
    }

}
