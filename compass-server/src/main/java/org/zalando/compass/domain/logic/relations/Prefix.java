package org.zalando.compass.domain.logic.relations;

import org.zalando.compass.domain.model.Relation;

import java.util.Comparator;

public final class Prefix implements Relation {

    private final Comparator<String> comparator = Comparator.comparing(String::length).reversed()
            .thenComparing(Comparator.naturalOrder());

    @Override
    public String getId() {
        return "prefix";
    }

    @Override
    public String getDescription() {
        return "Prefix";
    }

    @Override
    public int compare(final String left, final String right) {
        return comparator.compare(left, right);
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return requested.startsWith(configured);
    }

}
