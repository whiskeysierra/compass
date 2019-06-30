package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

import static org.zalando.compass.library.pagination.CursorCodec.CODEC;

@AllArgsConstructor
@EqualsAndHashCode
final class ForwardCursor<P, Q> implements Cursor<P, Q> {

    @Exclude
    private final AtomicReference<String> cache = new AtomicReference<>();

    @Getter
    private final P pivot;

    @Getter
    private final Q query;

    @Getter
    private final int limit;

    @Override
    public Cursor<P, Q> with(@Nullable final Q query, final int limit) {
        return this;
    }

    @Override
    public Cursor<P, Q> next(final P pivot) {
        return new ForwardCursor<>(pivot, query, limit);
    }

    @Override
    public Cursor<P, Q> previous(final P pivot) {
        return new BackwardCursor<>(pivot, query, limit);
    }

    @Override
    public Pagination<P> paginate() {
        return new ForwardPagination<>(pivot, limit);
    }

    @Override
    public String toString() {
        return cache.updateAndGet(cached -> cached == null ? CODEC.encode(this) : cached);
    }

}
