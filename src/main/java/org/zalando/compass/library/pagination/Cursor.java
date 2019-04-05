package org.zalando.compass.library.pagination;

import java.util.Map;

public interface Cursor<P> {

    Pagination<P> paginate(int limit);

    Cursor<P> withQuery(final Map<String, String> query);

    Cursor<P> next(final P pivot);

    Cursor<P> previous(final P pivot);

    static <P> Cursor<P> empty() {
        return new EmptyCursor<>();
    }

    static <P> Cursor<P> valueOf(final String cursor) {
        return CursorCodec.CODEC.decode(cursor);
    }

}
