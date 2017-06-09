package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;

public final class LessThan extends Inequality {

    @Override
    public String getId() {
        return "<";
    }

    @Override
    public String getTitle() {
        return "Less than";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values is strictly less than the configured one.";
    }

    @Override
    public boolean test(final JsonNode configured, final JsonNode requested) {
        return compare(requested, configured) < 0;
    }

}
