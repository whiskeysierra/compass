package org.zalando.compass.library.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ETagTest {

    @Test
    public void shouldWriteETag() {
        assertEquals("\"AAAAAAAAAAM\"", new ETag(3).toString());
    }

}
