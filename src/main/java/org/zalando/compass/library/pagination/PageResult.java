package org.zalando.compass.library.pagination;

import java.util.List;

public interface PageResult<T> {

    List<T> getElements();
    boolean hasNext();
    boolean hasPrevious();

    default T getHead() {
        return getElements().get(0);
    }

    default T getTail() {
        final List<T> elements = getElements();
        return elements.get(elements.size() - 1);
    }

    static <T> PageResult<T> create(final List<T> elements, final boolean next, final boolean previous) {
        return new DefaultPageResult<>(elements, next, previous);
    }

}
