package org.zalando.compass.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.zalando.compass.api.Dimension;

import java.util.Objects;

final class DefaultDimension implements Dimension {

    private final String id;

    @JsonCreator
    DefaultDimension(final String id) {
        this.id = id;
    }

    @JsonValue
    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof Dimension) {
            final Dimension other = (Dimension) that;
            return Objects.equals(id, other.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

}
