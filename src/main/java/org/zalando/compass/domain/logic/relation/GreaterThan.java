package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;

public final class GreaterThan extends Inequality {

    @Override
    public String getId() {
        return ">";
    }

    @Override
    public String getTitle() {
        return "Greater than";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values is strictly greater than the configured one.";
    }

    @Override
    public boolean test(final JsonNode configured, final JsonNode requested) {
        return compare(requested, configured) > 0;
    }

}
