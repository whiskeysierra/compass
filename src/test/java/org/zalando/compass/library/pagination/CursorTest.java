package org.zalando.compass.library.pagination;

import org.junit.Test;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CursorTest {

    @Test
    public void shouldCreateFromEmptyString() {
        final Cursor<Long> cursor = Cursor.valueOf("");

        assertEquals(Cursor.empty(), cursor);
    }

    @Test
    public void shouldCreateBackward() {
        final Cursor<Long> cursor = new DefaultCursor<>(Direction.BACKWARD, 3L, emptyMap());

        assertEquals("eyJkIjoiPCIsInAiOjN9", cursor.toString());
    }

    @Test
    public void shouldCreateForward() {
        final Cursor<Long> cursor = new DefaultCursor<>(Direction.FORWARD, 4L, emptyMap());

        assertEquals("eyJkIjoiPiIsInAiOjR9", cursor.toString());
    }

    @Test
    public void shouldParseBackward() {
        final Cursor<Long> actual = Cursor.valueOf("eyJkIjoiPCIsInAiOjN9");
        final Cursor<Long> expected = new DefaultCursor<>(Direction.BACKWARD, 3L, emptyMap());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseForward() {
        final Cursor<Long> actual = Cursor.valueOf("eyJkIjoiPiIsInAiOjR9");
        final Cursor<Long> expected = new DefaultCursor<>(Direction.FORWARD, 4L, emptyMap());

        assertEquals(expected, actual);
    }

}
