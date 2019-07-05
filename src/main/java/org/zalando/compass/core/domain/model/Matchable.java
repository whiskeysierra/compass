package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.MapDifference;
import org.zalando.compass.core.domain.api.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.google.common.collect.Maps.difference;
import static java.util.stream.Collectors.toList;

public interface Matchable<V extends Dimensional> {

    List<V> getValues();

    default V selectOne(final Map<Dimension, JsonNode> filter) {
        return select(filter).stream()
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    default List<V> select(final Map<Dimension, JsonNode> filter) {
        return getValues().stream()
                .filter(value -> match(value, filter))
                .collect(toList());
    }

    default boolean match(final V value, final Map<Dimension, JsonNode> filter) {
        final var diff = difference(value.getDimensions(), filter);
        final var unmatched = diff.entriesOnlyOnLeft();

        return unmatched.isEmpty() && common(diff) && differing(diff);
    }

    private boolean common(final MapDifference<Dimension, JsonNode> difference) {
        return difference.entriesInCommon().entrySet().stream().allMatch(entries(
                (dimension, node) -> dimension.test(node, node)));
    }

    private boolean differing(final MapDifference<Dimension, JsonNode> difference) {
        return difference.entriesDiffering().entrySet().stream().allMatch(entries(
                (dimension, diff) -> dimension.test(diff.leftValue(), diff.rightValue())));
    }

    private static <K, V> Predicate<Entry<K, V>> entries(final BiPredicate<K, V> predicate) {
        return e -> predicate.test(e.getKey(), e.getValue());
    }

}
