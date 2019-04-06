package org.zalando.compass.library.pagination;

import com.google.common.reflect.TypeToken;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

import static org.apiguardian.api.API.Status.DEPRECATED;

@SuppressWarnings("UnstableApiUsage") // TypeToken
public interface Cursor<P, Q> {

    @Nullable
    Q getQuery();

    default Cursor<P, Q> with(final int limit) {
        return with(null, limit);
    }

    // TODO can we use default values for non-initial cursors from spring?
    Cursor<P, Q> with(@Nullable final Q query, final int limit);

    Pagination<P> paginate();

    Cursor<P, Q> next(final P pivot);

    Cursor<P, Q> previous(final P pivot);

    static <P, Q> Cursor<P, Q> initial() {
        return new InitialCursor<>();
    }

    /**
     * @see org.springframework.core.convert.support.FallbackObjectToStringConverter
     * @param cursor encoded cursor
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @API(status = DEPRECATED)
    @SuppressWarnings("unused")
    static <P, Q> Cursor<P, Q> valueOf(final String cursor) {
        throw new UnsupportedOperationException();
    }

    static <P> Cursor<P, Void> valueOf(final String cursor, final Class<P> pivot) {
        return valueOf(cursor, TypeToken.of(pivot));
    }

    static <P> Cursor<P, Void> valueOf(final String cursor, final TypeToken<P> pivot) {
        return valueOf(cursor, pivot, TypeToken.of(Void.class));
    }

    static <P, Q> Cursor<P, Q> valueOf(final String cursor, final Class<P> pivot, final Class<Q> query) {
        return valueOf(cursor, TypeToken.of(pivot), TypeToken.of(query));
    }

    static <P, Q> Cursor<P, Q> valueOf(final String cursor, final TypeToken<P> pivot, final TypeToken<Q> query) {
        return CursorCodec.CODEC.decode(cursor, pivot, query);
    }

}
