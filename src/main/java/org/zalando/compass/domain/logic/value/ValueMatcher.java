package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

// TODO introduce "RichValue", "RichDimension" that reference each other + concrete relation rather than IDs everywhere
// TODO test in isolation?!
@Service
class ValueMatcher {

    private final DimensionService dimensionService;
    private final RelationService relationService;

    @Autowired
    ValueMatcher(
            final DimensionService dimensionService,
            final RelationService relationService) {
        this.dimensionService = dimensionService;
        this.relationService = relationService;
    }

    List<Value> match(final List<Value> values, final Map<String, JsonNode> filter) {
        // TODO is there a nicer way to express this?
        final Collection<Matcher> matchers = readDimensionsFor(values);

        return match(values, filter, matchers);
    }

    // TODO could be its own service?
    private Collection<Matcher> readDimensionsFor(final Collection<Value> values) {
        return correlateWithRelations(deriveDimensions(values));
    }

    @lombok.Value
    private final class Matcher implements BiPredicate<JsonNode, JsonNode> {

        String dimensionId;
        Relation relation;

        @Override
        public boolean test(final JsonNode left, final JsonNode right) {
            return relation.test(left, right);
        }
    }

    private List<Dimension> deriveDimensions(final Collection<Value> values) {
        final Set<String> used = values.stream()
                .flatMap(value -> value.getDimensions().keySet().stream())
                .collect(toSet());

        return dimensionService.readAll(used);
    }

    private Collection<Matcher> correlateWithRelations(final Collection<Dimension> dimensions) {
        return dimensions.stream()
                .map(dimension -> new Matcher(dimension.getId(), relationService.read(dimension.getRelation())))
                .collect(toList());
    }

    private List<Value> match(final List<Value> values, final Map<String, JsonNode> filter,
            final Collection<Matcher> matchers) {

        if (matchers.isEmpty()) {
            return values;
        }

        final Predicate<Value> predicate = matchers.stream()
                .map(matcher -> matcher(filter, matcher))
                .reduce(Predicate::and).orElseThrow(AssertionError::new);

        return values.stream().filter(predicate).collect(toList());
    }

    private Predicate<Value> matcher(final Map<String, JsonNode> filter, final Matcher matcher) {
        return value -> {
            @Nullable final JsonNode configured = value.getDimensions().get(matcher.getDimensionId());
            @Nullable final JsonNode requested = filter.get(matcher.getDimensionId());

            // TODO break this up and make it more readable
            return configured == null // TODO is that even correct?!
                    || requested != null && !requested.isNull() && matcher.test(configured, requested);
        };
    }

}
