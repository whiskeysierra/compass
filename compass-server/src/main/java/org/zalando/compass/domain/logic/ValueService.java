package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.collect.Ordering.explicit;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class ValueService {

    private final ValueRepository valueRepository;
    private final DimensionRepository dimensionRepository;
    private final RelationService relationService;

    @Autowired
    public ValueService(final ValueRepository valueRepository, final DimensionRepository dimensionRepository,
            final RelationService relationService) {
        this.valueRepository = valueRepository;
        this.dimensionRepository = dimensionRepository;
        this.relationService = relationService;
    }

    public void createOrUpdate(final String key, final Values values) {
        valueRepository.create(key, values.getValues());
        // TODO or update?!
    }

    public Value read(final String key, final Map<String, String> filter) {
        return readAll(key, filter).getValues().stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

    public Values readAll(final String key, final Map<String, String> filter) {
        final List<Value> values = valueRepository.readAll(key);
        final Map<Dimension, Relation> dimensions = readDimensions();

        values.sort(byDimensionSizeDescending()
                .thenComparing(byDimensionsLexicographically(dimensions))
                .thenComparing(byDimensionValues(dimensions)));

        if (filter.isEmpty()) {
            return new Values(values);
        }

        return values.stream()
                .filter(byMatch(filter, dimensions))
                .collect(collectingAndThen(toList(), Values::new));
    }

    private Map<Dimension, Relation> readDimensions() {
        final Map<String, Relation> relations = relationService.readAll().stream()
                .collect(toMap(Relation::getId, identity()));

        return dimensionRepository.readAll().stream()
                .collect(toMap(identity(), dimension -> relations.get(dimension.getRelation()),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    private Comparator<Value> byDimensionSizeDescending() {
        return comparing(Value::getDimensions, comparing(Map::size)).reversed();
    }

    private Comparator<Value> byDimensionsLexicographically(final Map<Dimension, ?> dimensions) {
        return comparing(value -> value.getDimensions().keySet(), explicit(dimensions.keySet().stream()
                .map(Dimension::getId).collect(toList())).lexicographical());
    }

    private Comparator<Value> byDimensionValues(final Map<Dimension, Relation> dimensions) {
        return comparing(Value::getDimensions, dimensions.entrySet().stream()
                .map(this::byDimensionValue)
                .reduce(this::startWithTie, Comparator::thenComparing));
    }

    private Comparator<Map<String, JsonNode>> byDimensionValue(final Entry<Dimension, Relation> entry) {
        final Dimension dimension = entry.getKey();
        final Relation relation = entry.getValue();
        return comparing(get(dimension), nullsLast(comparing(JsonNode::asText, relation)));
    }

    private Function<Map<String, JsonNode>, JsonNode> get(final Dimension dimension) {
        return map -> map.get(dimension.getId());
    }

    private <T> int startWithTie(@SuppressWarnings("unused") final T l, @SuppressWarnings("unused") final T r) {
        return 0;
    }

    private Predicate<Value> byMatch(final Map<String, String> filter, final Map<Dimension, Relation> dimensions) {
        return dimensions.entrySet().stream()
                .map(entry -> match(filter, entry))
                .reduce(Predicate::and).orElse(v -> false); // TODO verify that this shouldn't be true
    }

    private Predicate<Value> match(final Map<String, String> filter, final Entry<Dimension, Relation> entry) {
        final Dimension dimension = entry.getKey();
        final Relation relation = entry.getValue();
        return value -> {
            @Nullable final JsonNode configured = value.getDimensions().get(dimension.getId());
            @Nullable final String requested = filter.get(dimension.getId());

            return configured == null
                    || requested != null && relation.test(configured.asText(), requested);
        };
    }

    @Transactional
    public void replace(final String key, final Values values) throws IOException {
        delete(key, emptyMap());
        createOrUpdate(key, values);
    }

    public void delete(final String key, final Map<String, Object> filter) throws IOException {
        valueRepository.delete(key, filter);
    }

}
