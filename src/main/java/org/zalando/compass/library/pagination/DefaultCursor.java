package org.zalando.compass.library.pagination;

import com.google.common.base.MoreObjects;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Collections.emptyMap;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.zalando.compass.library.pagination.CursorCodec.CODEC;

// TODO polymorphic backward vs forward cursor?
@EqualsAndHashCode
final class DefaultCursor<P> implements Cursor<P> {

    @EqualsAndHashCode.Exclude
    private final AtomicReference<String> cache = new AtomicReference<>();

    @Getter
    private final Direction direction;

    @Getter
    private final P pivot;

    @Getter
    private final Map<String, String> query;

    @API(status = INTERNAL)
    DefaultCursor(final Direction direction, final P pivot, final @Nullable Map<String, String> query) {
        this.direction = direction;
        this.pivot = pivot;
        this.query = firstNonNull(query, emptyMap());
    }

    @Override
    public Cursor<P> withQuery(Map<String, String> query) {
        return new DefaultCursor<>(direction, pivot, query);
    }

    @Override
    public Cursor<P> next(P pivot) {
        return new DefaultCursor<>(Direction.FORWARD, pivot, query);
    }

    @Override
    public Cursor<P> previous(P pivot) {
        return new DefaultCursor<>(Direction.BACKWARD, pivot, query);
    }

    @Override
    public Pagination<P> paginate(int limit) {
        if (direction == Direction.FORWARD) {
            return new ForwardPagination<>(pivot, limit);
        }

        return new BackwardPagination<>(pivot, limit);
    }

    @Override
    public String toString() {
        return cache.updateAndGet(cached -> cached == null ? CODEC.encode(this) : cached);
    }

}
