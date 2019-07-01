package org.zalando.compass.core.domain.api;

public final class NotFoundException extends RuntimeException {

    public NotFoundException() {
    }

    public NotFoundException(final String message) {
        super(message);
    }

    public static void exists(final boolean condition, final String message, final Object... arguments) {
        if (!condition) {
            throw new NotFoundException(String.format(message, arguments));
        }
    }

}
