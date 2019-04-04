package org.zalando.compass.library.pagination;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CursorTest {

    @Test
    public void shouldCreateFromNull() {
        final Cursor<Long> cursor = Cursor.valueOf(null);

        assertNull(cursor.getDirection());
        assertNull(cursor.getPivot());
    }

    @Test
    public void shouldCreateFromEmptyString() {
        final Cursor<Long> cursor = Cursor.valueOf("");

        assertNull(cursor.getDirection());
        assertNull(cursor.getPivot());
    }

    @Test
    public void shouldCreateBackward() {
        final Cursor<Long> cursor = Cursor.create(Direction.BACKWARD, 3L);

        assertEquals("eyJkIjoiPCIsInAiOjN9", cursor.toString());
    }

    @Test
    public void shouldCreateForward() {
        final Cursor<Long> cursor = Cursor.create(Direction.FORWARD, 4L);

        assertEquals("eyJkIjoiPiIsInAiOjR9", cursor.toString());
    }

    @Test
    public void shouldParseBackward() {
        final Cursor<Long> cursor = Cursor.valueOf("eyJkIjoiPCIsInAiOjN9");

        assertEquals(Direction.BACKWARD, cursor.getDirection());
        assertEquals(3L, cursor.getPivot());
    }

    @Test
    public void shouldParseForward() {
        final Cursor<Long> cursor = Cursor.valueOf("eyJkIjoiPiIsInAiOjR9");

        assertEquals(Direction.FORWARD, cursor.getDirection());
        assertEquals(4L, cursor.getPivot());
    }

}
