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
    public String getTitle() {
        return "Longest Prefix Match";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values shares the longest prefix with the configured one. " +
                "In case of multiple candidates it will match the longest value " +
                "with a fallback to the least (natural order). " +
                "Prefix matching is useful for data structures that have a natural hierarchy, including " +
                "but not limited to locales, geohashes and IP subnet masks.";
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
