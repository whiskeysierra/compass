package org.zalando.compass.library.pagination;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CursorTest {

    @Test
    public void shouldParseFromEmptyString() {
        final var actual = Cursor.valueOf("", String.class);
        final Cursor<String, Void> expected = Cursor.initial();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldRenderBackwardCursor() {
        final Cursor<Long, Void> cursor = new BackwardCursor<>(3L, null, 25);

        assertEquals("eyJkIjoiPCIsInAiOjMsImwiOjI1fQ", cursor.toString());
    }

    @Test
    public void shouldRenderForwardCursor() {
        final Cursor<Long, Void> cursor = new ForwardCursor<>(4L, null, 25);

        assertEquals("eyJkIjoiPiIsInAiOjQsImwiOjI1fQ", cursor.toString());
    }

    @Test
    public void shouldRenderBackwardCursorWithQuery() {
        final Cursor<Long, String> cursor = new BackwardCursor<>(3L, "test", 25);

        assertEquals("eyJkIjoiPCIsInAiOjMsInEiOiJ0ZXN0IiwibCI6MjV9", cursor.toString());
    }

    @Test
    public void shouldRenderForwardCursorWithQuery() {
        final Cursor<Long, String> cursor = new ForwardCursor<>(4L, "test", 25);

        assertEquals("eyJkIjoiPiIsInAiOjQsInEiOiJ0ZXN0IiwibCI6MjV9", cursor.toString());
    }

    @Test
    public void shouldParseBackwardCursor() {
        final var actual = Cursor.valueOf("eyJkIjoiPCIsInAiOjMsImwiOjI1fQ", Long.class);
        final Cursor<Long, Void> expected = new BackwardCursor<>(3L, null, 25);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseForwardCursor() {
        final var actual = Cursor.valueOf("eyJkIjoiPiIsInAiOjQsImwiOjI1fQ", Long.class);
        final Cursor<Long, Void> expected = new ForwardCursor<>(4L, null, 25);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseBackwardCursorWithQuery() {
        final var actual = Cursor.valueOf("eyJkIjoiPCIsInAiOjMsInEiOiJ0ZXN0IiwibCI6MjV9", Long.class, String.class);
        final Cursor<Long, String> expected = new BackwardCursor<>(3L, "test", 25);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseForwardCursorWithQuery() {
        final var actual = Cursor.valueOf("eyJkIjoiPiIsInAiOjQsInEiOiJ0ZXN0IiwibCI6MjV9", Long.class, String.class);
        final Cursor<Long, String> expected = new ForwardCursor<>(4L, "test", 25);

        assertEquals(expected, actual);
    }

}
