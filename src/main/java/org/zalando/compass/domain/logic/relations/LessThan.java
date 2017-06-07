package org.zalando.compass.domain.logic.relations;

import org.zalando.compass.domain.model.Relation;

public final class LessThan implements Relation {

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
        return "Matches values where the requested dimension values is strictly less than the configured one. " +
                "In case of multiple candidates it will match the least (natural order).";
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return requested.compareTo(configured) < 0;
    }

}
