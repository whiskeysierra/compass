package org.zalando.compass.core.domain.model.relation;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Relation;

public final class PrefixMatch implements Relation {

    @Override
    public String getId() {
        return "^";
    }

    @Override
    public String getTitle() {
        return "Prefix match";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values shares the longest prefix with the configured one. " +
                "Prefix matching is useful for data structures that have a natural hierarchy, including " +
                "but not limited to locales, geohashes and IP subnet masks.";
    }

    @Override
    public boolean test(final JsonNode configured, final JsonNode requested) {
        return requested.asText().startsWith(configured.asText());
    }

    @Override
    public String toString() {
        return getId();
    }

}
