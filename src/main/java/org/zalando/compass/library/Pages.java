package org.zalando.compass.library;

import org.zalando.compass.domain.model.Page;

import java.util.List;

public final class Pages {

    public static <T> Page<T> page(final List<T> elements, final int limit) {
        if (elements.size() > limit) {
            final List<T> items = elements.subList(0, limit);
            final T next = items.get(items.size() - 1);
            return new Page<>(items, next);
        } else {
            return new Page<>(elements, null);
        }
    }

}
