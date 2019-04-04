package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public interface Cursor<P> {

    @Nullable
    Direction getDirection();

    @Nullable
    P getPivot();

    Map<String, String> getQuery();

    default Cursor<P> withQuery(final Map<String, String> query) {
        return create(getDirection(), getPivot(), query);
    }

    default Cursor<P> next(final P pivot) {
        return create(Direction.FORWARD, pivot, getQuery());
    }

    default Cursor<P> previous(final P pivot) {
        return create(Direction.BACKWARD, pivot, getQuery());
    }

    default P before() {
        return getDirection() == Direction.BACKWARD ? getPivot() : null;
    }

    default P after() {
        return getDirection() == Direction.FORWARD ? getPivot() : null;
    }

    static <P> Cursor<P> empty() {
        return create(null, null);
    }

    static <P> Cursor<P> create(@Nullable final Direction direction, @Nullable final P pivot) {
        return create(direction, pivot, Collections.emptyMap());
    }

    static <P> Cursor<P> create(@Nullable final Direction direction, @Nullable final P pivot, final Map<String, String> query) {
        return new DefaultCursor<>(direction, pivot, query);
    }

    static <P> Cursor<P> valueOf(final String cursor) {
        return CursorCodec.CODEC.decode(cursor);
    }

}
