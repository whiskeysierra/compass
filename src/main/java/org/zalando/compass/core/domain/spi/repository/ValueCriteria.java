package org.zalando.compass.core.domain.spi.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.core.domain.model.Dimension;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
public class ValueCriteria {

    private final String key;
    private final Dimension dimension;

    public static ValueCriteria byKey(final String key) {
        return new ValueCriteria(key, null);
    }

    public static ValueCriteria byDimension(final Dimension dimension) {
        return new ValueCriteria(null, dimension);
    }

}
