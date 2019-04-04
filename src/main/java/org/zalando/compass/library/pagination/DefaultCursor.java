package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;

import static org.zalando.compass.library.pagination.CursorCodec.CODEC;

@AllArgsConstructor
public final class DefaultCursor<P> implements Cursor<P> {

    private final AtomicReference<String> cache = new AtomicReference<>();

    @Getter
    private final Direction direction;

    @Getter
    private final P pivot;

    @Override
    public String toString() {
        return cache.updateAndGet(cached -> cached == null ? CODEC.encode(this) : cached);
    }

}
