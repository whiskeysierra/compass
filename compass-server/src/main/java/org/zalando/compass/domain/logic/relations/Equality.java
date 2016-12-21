package org.zalando.compass.domain.logic.relations;

import org.zalando.compass.domain.model.Relation;

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
        return "Matches values where the requested dimension values is equal to the configured one.";
    }

    @Override
    public int compare(final String left, final String right) {
        return left.compareTo(right);
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return configured.equals(requested);
    }

}
