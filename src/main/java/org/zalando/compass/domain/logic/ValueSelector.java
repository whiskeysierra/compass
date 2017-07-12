package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Dimensional;
import org.zalando.compass.domain.persistence.DimensionRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.collect.ImmutableBiMap.toImmutableBiMap;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Sets.union;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
class ValueSelector {

    private final DimensionRepository dimensionRepository;
    private final RelationService relationService;
    private final ValueMatcher matcher;

    ValueSelector(final DimensionRepository dimensionRepository, final RelationService relationService,
            final ValueMatcher matcher) {
        this.dimensionRepository = dimensionRepository;
        this.relationService = relationService;
        this.matcher = matcher;
    }

    <V extends Dimensional> List<V> select(final List<V> rawValues, final Map<String, JsonNode> rawFilter) {
        final Map<String, RichDimension> map = findDimensions(rawValues, rawFilter);

        final BiMap<V, Map<RichDimension, JsonNode>> values = wrap(rawValues, map::get);
        final Map<RichDimension, JsonNode> filter = wrap(rawFilter, map);

        final List<Map<RichDimension, JsonNode>> match = matcher.match(values.values(), filter);

        return match.stream()
                .map(values.inverse()::get)
                .collect(toList());
    }

    private Map<String, RichDimension> findDimensions(final List<? extends Dimensional> values, final Map<String, JsonNode> filter) {
        final Set<String> dimensionIds = union(
                values.stream()
                        .flatMap(value -> value.getDimensions().keySet().stream())
                        .collect(toSet()),
                filter.keySet());

        return dimensionRepository.findAll(dimensionIds).stream()
                .collect(toMap(Dimension::getId, dimension -> new RichDimension(
                        dimension.getId(),
                        dimension.getSchema(),
                        relationService.read(dimension.getRelation()),
                        dimension.getDescription()
                )));
    }

    private <V extends Dimensional> BiMap<V, Map<RichDimension, JsonNode>> wrap(final List<V> values,
            final Function<String, RichDimension> lookup) {
        return values.stream()
                .collect(toImmutableBiMap(identity(),
                        value -> value.getDimensions().entrySet().stream()
                        .collect(toImmutableMap(e -> lookup.apply(e.getKey()), Map.Entry::getValue))));
    }

    private ImmutableMap<RichDimension, JsonNode> wrap(final Map<String, JsonNode> rawFilter, final Map<String, RichDimension> lookup) {
        final ImmutableMap.Builder<RichDimension, JsonNode> builder = ImmutableMap.builder();

        rawFilter.forEach((dimensionId, dimensionValue) -> {
            @Nullable final RichDimension dimension = lookup.get(dimensionId);
            if (dimension != null) {
                builder.put(dimension, dimensionValue);
            }
        });

        return builder.build();
    }

}
