package org.zalando.compass.domain.logic.relations;

import org.zalando.compass.domain.model.Relation;

import java.util.Comparator;
import java.util.regex.Pattern;

public final class Matches implements Relation {

    private final Comparator<String> comparator = Comparator.comparing(String::length).reversed()
            .thenComparing(Comparator.naturalOrder());

    @Override
    public String getId() {
        return "~";
    }

    @Override
    public String getDescription() {
        return "Matches";
    }

    @Override
    public int compare(final String left, final String right) {
        return comparator.compare(left, right);
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return Pattern.compile(configured).matcher(requested).matches();
    }

}
