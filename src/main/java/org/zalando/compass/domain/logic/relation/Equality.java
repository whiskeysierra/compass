package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonType;
import org.zalando.compass.domain.model.Relation;

import java.util.EnumSet;
import java.util.Set;

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
    public Set<JsonType> supports() {
        return EnumSet.allOf(JsonType.class);
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
