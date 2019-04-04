package org.zalando.compass.library.pagination;

import javax.annotation.Nullable;

public interface Cursor<P> {

    @Nullable
    Direction getDirection();

    @Nullable
    P getPivot();

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
        return new DefaultCursor<>(direction, pivot);
    }

    static <P> Cursor<P> valueOf(final String cursor) {
        return CursorCodec.CODEC.decode(cursor);
    }

}
