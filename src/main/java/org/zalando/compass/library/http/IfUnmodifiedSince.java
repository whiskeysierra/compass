package org.zalando.compass.library.http;

import javax.annotation.Nullable;
import java.time.Instant;

import static java.time.Instant.ofEpochMilli;

public abstract class IfUnmodifiedSince implements Condition<Instant> {

    @Nullable
    public static Condition<Instant> valueOf(final long timestamp) {
        if (timestamp == -1) {
            return null;
        }

        final Instant instant = ofEpochMilli(timestamp);
        final Condition<Instant> isEqual = instant::equals;
        final Condition<Instant> isAfter = instant::isAfter;
        return isEqual.or(isAfter)::test;
    }

}
