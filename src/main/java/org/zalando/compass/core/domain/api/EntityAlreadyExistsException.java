package org.zalando.compass.core.domain.api;

public final class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException() {
        // nothing to do
    }

    public EntityAlreadyExistsException(final String message) {
        super(message);
    }

}
