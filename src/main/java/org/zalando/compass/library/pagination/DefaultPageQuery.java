package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class DefaultPageQuery<C> implements PageQuery<C> {

    C pivot;

    @Wither
    int limit;

    Direction direction;

    @Override
    public PageQuery<C> increment() {
        return withLimit(limit + 1);
    }

}
