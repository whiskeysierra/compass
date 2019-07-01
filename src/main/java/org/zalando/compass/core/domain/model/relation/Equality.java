package org.zalando.compass.core.domain.model.relation;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.kernel.domain.model.Relation;

public final class Equality implements Relation {

    @Override
    public String getId() {
        return "=";
    }

    @Override
    public String getTitle() {
        return "Equality";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values are equal to the configured ones.";
    }

    @Override
    public boolean test(final JsonNode configured, final JsonNode requested) {
        return configured.equals(requested);
    }

    @Override
    public String toString() {
        return getId();
    }

}
