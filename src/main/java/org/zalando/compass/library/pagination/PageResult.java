package org.zalando.compass.library.pagination;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;

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

    interface Pager<T, P> {
        P page(@Nullable URI next, @Nullable URI prev, List<T> elements);
    }

    // TODO PageResult<R> map(Function<T, R> mapper)

    default <C, P, F> P render(
            final Pager<T, P> pager,
            final Cursor<C, F> cursor,
            final Function<T, C> id,
            final Function<Cursor<C, F>, URI> linker) {
        return render(pager, cursor, id, linker, identity());
    }

    default <C, P, N, F> P render(
            final Pager<N, P> pager,
            final Cursor<C, F> cursor,
            final Function<T, C> id,
            final Function<Cursor<C, F>, URI> linker,
            final Function<T, N> mapper) {
        return pager.page(
                hasNext() ? linker.apply(cursor.next(id.apply(getTail()))) : null,
                hasPrevious() ? linker.apply(cursor.previous(id.apply(getHead()))) : null,
                getElements().stream().map(mapper).collect(Collectors.toList())
        );
    }

    static <T> PageResult<T> empty() {
        return create(emptyList(), false, false);
    }

    static <T> PageResult<T> create(final List<T> elements, final boolean next, final boolean previous) {
        return new DefaultPageResult<>(elements, next, previous);
    }

}
