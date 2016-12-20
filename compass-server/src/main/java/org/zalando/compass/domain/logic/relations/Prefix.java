package org.zalando.compass.domain.logic.relations;

import com.google.common.collect.ComparisonChain;
import org.zalando.compass.domain.model.Relation;

import static java.util.Comparator.reverseOrder;

public final class Prefix implements Relation {

    @Override
    public String getId() {
        return "^";
    }

    @Override
    public String getDescription() {
        return "Prefix";
    }

    @Override
    public int compare(final String left, final String right) {
        return ComparisonChain.start()
                .compare(left.length(), right.length(), reverseOrder())
                .compare(left, right)
                .result();
    }

    @Override
    public boolean test(final String configured, final String requested) {
        return requested.startsWith(configured);
    }

}
