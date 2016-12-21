package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;
import org.zalando.compass.library.SchemaValidator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.collect.Ordering.explicit;
import static java.util.Collections.singleton;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class ValueService {

    private final SchemaValidator validator;
    private final RelationService relationService;
    private final DimensionRepository dimensionRepository;
    private final KeyService keyService;
    private final ValueRepository valueRepository;

    @Autowired
    public ValueService(final SchemaValidator validator, final RelationService relationService,
            final DimensionRepository dimensionRepository,
            final KeyService keyService, final ValueRepository valueRepository) {
        this.validator = validator;
        this.relationService = relationService;
        this.dimensionRepository = dimensionRepository;
        this.keyService = keyService;
        this.valueRepository = valueRepository;
    }

    public void createOrUpdate(final String key, final Value value) {
        validateDimensions(value);
        validateValue(key, value);

        valueRepository.createOrUpdate(key, singleton(value));
    }

    private void validateDimensions(final Value value) {
        final ImmutableMap<String, JsonNode> dimensions = value.getDimensions();

        for (final Dimension dimension : dimensionRepository.read(dimensions.keySet())) {
            final JsonNode schema = dimension.getSchema();
            final JsonNode node = dimensions.get(dimension.getId());
            validator.validate(schema, node);
        }
    }

    private void validateValue(final String key, final Value value) {
        final JsonNode schema = keyService.read(key).getSchema();
        final JsonNode node = value.getValue();
        validator.validate(schema, node);
    }

    public Value read(final String key, final Map<String, String> filter) {
        return readAll(key, filter).getValues().stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

    public Values readAll(final String key, final Map<String, String> filter) {
        checkKeyExists(key);

        final List<Value> values = valueRepository.readAll(key);
        final Map<Dimension, Relation> dimensions = readDimensions();

        sort(values, dimensions);

        return new Values(match(values, dimensions, filter));
    }

    public Values findAll(final String pattern) {
        final List<Value> values = valueRepository.findAll(pattern);
        final Map<Dimension, Relation> dimensions = readDimensions();

        sort(values, dimensions);

        return new Values(values);
    }

    private void checkKeyExists(final String key) {
        if (!keyService.exists(key)) {
            throw new NotFoundException();
        }
    }

    private Map<Dimension, Relation> readDimensions() {
        final Map<String, Relation> relations = relationService.readAll().stream()
                .collect(toMap(Relation::getId, identity()));

        return dimensionRepository.readAll().stream()
                .collect(toMap(identity(), dimension -> relations.get(dimension.getRelation()),
                        this::denyDuplicates, LinkedHashMap::new));
    }

    private <T> T denyDuplicates(final T u, @SuppressWarnings("unused") final T v) {
        throw new IllegalStateException(String.format("Duplicate key %s", u));
    }

    private void sort(final List<Value> values, final Map<Dimension, Relation> dimensions) {
        values.sort(byDimensionSizeDescending()
                .thenComparing(byDimensionsLexicographically(dimensions))
                .thenComparing(byDimensionValues(dimensions)));
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
        return comparing(getValueFor(dimension), nullsLast(comparing(JsonNode::asText, relation)));
    }

    private Function<Map<String, JsonNode>, JsonNode> getValueFor(final Dimension dimension) {
        return map -> map.get(dimension.getId());
    }

    private <T> int startWithTie(@SuppressWarnings("unused") final T l, @SuppressWarnings("unused") final T r) {
        return 0;
    }

    private List<Value> match(final List<Value> values, final Map<Dimension, Relation> dimensions,
            final Map<String, String> filter) {
        return values.stream()
                .filter(byMatch(dimensions, filter))
                .collect(toList());
    }

    private Predicate<Value> byMatch(final Map<Dimension, Relation> dimensions, final Map<String, String> filter) {
        return dimensions.entrySet().stream()
                .map(entry -> match(filter, entry))
                .reduce(Predicate::and).orElse(v -> true);
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

    public void delete(final String key, final Map<String, String> filter) throws IOException {
        valueRepository.delete(key, filter);
    }

}
