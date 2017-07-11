package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class DefaultPageQuery<C> implements PageQuery<C> {

    C pivot;
    int limit;
    Direction direction;

    @Override
    public PageQuery<C> increment() {
        return new DefaultPageQuery<>(pivot, limit + 1, direction);
    }

}
