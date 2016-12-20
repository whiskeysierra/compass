package org.zalando.compass.domain.logic.relations;

import org.zalando.compass.domain.model.Relation;

public final class GreaterThanOrEqual implements Relation {

    @Override
    public String getId() {
        return "≥";
    }

    @Override
    public String getDescription() {
        return "Greater than or equal";
    }

    @Override
    public int compare(final String left, final String right) {
        return right.compareTo(left);
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return compare(configured, requested) >= 0;
    }

}
