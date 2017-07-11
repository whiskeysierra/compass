package org.zalando.compass.library.pagination;

public final class IllegalPageQueryException extends RuntimeException {

    public IllegalPageQueryException(final String message) {
        super(message);
    }

}
