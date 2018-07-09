package org.zalando.compass.domain.logic;

public final class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException() {
        // nothing to do
    }

    public EntityAlreadyExistsException(final String message) {
        super(message);
    }

}
