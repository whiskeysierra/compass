package org.zalando.compass.domain.logic.relations;

import com.fasterxml.jackson.databind.JsonNode;

public final class GreaterThanOrEqual extends Inequality {

    @Override
    public String getId() {
        return ">=";
    }

    @Override
    public String getTitle() {
        return "Greater than or equal";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values is greater than or equal to the configured one.";
    }

    @Override
    public boolean test(final JsonNode configured, final JsonNode requested) {
        return compare(requested, configured) >= 0;
    }

}
