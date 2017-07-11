package org.zalando.compass.library.pagination;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
final class DefaultPageResult<T> implements PageResult<T> {

    @Getter
    List<T> elements;

    boolean next;
    boolean previous;

    @Override
    public boolean hasNext() {
        return next;
    }

    @Override
    public boolean hasPrevious() {
        return previous;
    }

}
