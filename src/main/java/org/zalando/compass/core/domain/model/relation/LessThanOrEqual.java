package org.zalando.compass.core.domain.model.relation;

import com.fasterxml.jackson.databind.JsonNode;

public final class LessThanOrEqual extends Inequality {

    @Override
    public String getId() {
        return "<=";
    }

    @Override
    public String getTitle() {
        return "Less than or equal";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values is less than or equal to the configured one.";
    }

    @Override
    public boolean test(final JsonNode configured, final JsonNode requested) {
        return compare(requested, configured) <= 0;
    }

}
