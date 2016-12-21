package org.zalando.compass.domain.logic.relations;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ComparisonChain;
import org.zalando.compass.domain.model.Relation;

import java.util.regex.Pattern;

import static com.google.common.cache.CacheLoader.from;
import static java.util.Comparator.reverseOrder;

public final class Matches implements Relation {

    private final LoadingCache<String, Pattern> cache = CacheBuilder.newBuilder()
            // not 100% sure why we need that cast here
            .build(from((Function<String, Pattern>) Pattern::compile));

    @Override
    public String getId() {
        return "~";
    }

    @Override
    public String getTitle() {
        return "Matches";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values matches the configured regular expression. " +
                "In case of multiple candidates it will match the longest value " +
                "with a fallback to the least (natural order).";
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
        return compile(configured).matcher(requested).matches();
    }

    private Pattern compile(final String pattern) {
        return cache.getUnchecked(pattern);
    }

}
