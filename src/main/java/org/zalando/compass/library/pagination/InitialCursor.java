package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;

@AllArgsConstructor
@EqualsAndHashCode
final class InitialCursor<P, Q> implements Cursor<P, Q> {

    @Getter
    private final Q query;
    private final Integer limit;

    InitialCursor() {
        this(null, null);
    }

    @Override
    public Cursor<P, Q> with(@Nullable final Q query, final int limit) {
        return new InitialCursor<>(query, limit);
    }

    @Override
    public Cursor<P, Q> next(P pivot) {
        return new ForwardCursor<>(pivot, query, limit);
    }

    @Override
    public Cursor<P, Q> previous(P pivot) {
        return new BackwardCursor<>(pivot, query, limit);
    }

    @Override
    public Pagination<P> paginate() {
        return new InitialPagination<>(limit);
    }

}
