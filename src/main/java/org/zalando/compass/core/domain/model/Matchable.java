package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import org.zalando.compass.core.domain.api.NotFoundException;

import java.util.List;
import java.util.Map;

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
        final List<V> values = getValues();

        return values.stream()
                .filter(value -> {
                    final MapDifference<Dimension, JsonNode> diff = difference(value.getDimensions(), filter);

                    final Map<Dimension, JsonNode> onlyOnLeft = diff.entriesOnlyOnLeft();
                    final Map<Dimension, JsonNode> common = diff.entriesInCommon();
                    final Map<Dimension, ValueDifference<JsonNode>> differing = diff.entriesDiffering();

                    if (!onlyOnLeft.isEmpty()) {
                        return false;
                    }

                    for (final Dimension dimension : common.keySet()) {
                        final JsonNode node = common.get(dimension);
                        final Relation relation = dimension.getRelation();

                        if (relation.test(node, node)) {
                            continue;
                        }

                        return false;
                    }

                    for (final Dimension dimension : differing.keySet()) {
                        final ValueDifference<JsonNode> difference = differing.get(dimension);
                        final Relation relation = dimension.getRelation();
                        final JsonNode configured = difference.leftValue();
                        final JsonNode requested = difference.rightValue();

                        if (relation.test(configured, requested)) {
                            continue;
                        }

                        return false;
                    }

                    return true;
                })
                .collect(toList());
    }

}
