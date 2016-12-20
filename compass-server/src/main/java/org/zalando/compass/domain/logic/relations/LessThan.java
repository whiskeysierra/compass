package org.zalando.compass.domain.logic.relations;

import org.zalando.compass.domain.model.Relation;

public final class LessThan implements Relation {

    @Override
    public String getId() {
        return "<";
    }

    @Override
    public String getDescription() {
        return "Less than";
    }

    @Override
    public int compare(final String left, final String right) {
        return left.compareTo(right);
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return requested.compareTo(configured) < 0;
    }

}
