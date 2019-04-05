package org.zalando.compass.library.pagination;

import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Map;

@EqualsAndHashCode
final class EmptyCursor<P> implements Cursor<P> {

    // TODO remove!
    @Override
    public Cursor<P> withQuery(Map<String, String> query) {
        return this;
    }

    @Override
    public Cursor<P> next(P pivot) {
        return new DefaultCursor<>(Direction.FORWARD, pivot, Collections.emptyMap());
    }

    @Override
    public Cursor<P> previous(P pivot) {
        return new DefaultCursor<>(Direction.BACKWARD, pivot, Collections.emptyMap());
    }

    @Override
    public Pagination<P> paginate(int limit) {
        return new InitialPagination<>(limit);
    }

    @Override
    public String toString() {
        return "";
    }
}
