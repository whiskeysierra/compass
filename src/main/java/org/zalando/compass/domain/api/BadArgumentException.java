package org.zalando.compass.domain.api;

public final class BadArgumentException extends RuntimeException {

    public BadArgumentException(final String message) {
        super(message);
    }

    public BadArgumentException(final Throwable cause) {
        super(cause);
    }

    public static void checkArgument(final boolean condition, final String message, final Object... arguments) {
        if (!condition) {
            throw new BadArgumentException(String.format(message, arguments));
        }
    }

}
