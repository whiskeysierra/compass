package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
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
