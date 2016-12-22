package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Realization;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueCriteria;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ArrayListMultimap.create;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Ordering.explicit;
import static com.google.common.collect.Sets.difference;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.zalando.compass.domain.persistence.DimensionCriteria.dimensions;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKeyPattern;
import static org.zalando.compass.domain.persistence.ValueCriteria.withoutCriteria;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

@Service
public class ValueService {

    private final SchemaValidator validator;
    private final RelationRepository relationRepository;
    private final DimensionRepository dimensionRepository;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    @Autowired
    public ValueService(final SchemaValidator validator, final RelationRepository relationRepository,
            final DimensionRepository dimensionRepository,
            final KeyRepository keyRepository, final ValueRepository valueRepository) {
        this.validator = validator;
        this.relationRepository = relationRepository;
        this.dimensionRepository = dimensionRepository;
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
    }

    public boolean createOrUpdate(final Value next) throws IOException {
        validateDimensions(next);
        validateValue(next);

        final Realization id = new Realization(next.getKey(), next.getDimensions());
        @Nullable final Value current = valueRepository.find(id).orElse(null);

        if (current == null) {
            return valueRepository.create(next);
        } else {
            valueRepository.update(next);
        }

        return false;
    }

    private void validateDimensions(final Value value) throws IOException {
        final ImmutableSet<String> dimensions = value.getDimensions().keySet();
        final List<Dimension> read = dimensionRepository.findAll(dimensions(dimensions));

        final Set<String> difference = difference(dimensions, read.stream().map(Dimension::getId).collect(toSet()));
        checkArgument(difference.isEmpty(), "Unknown dimensions: " + difference);

        validator.validate(read, value);
    }

    private void validateValue(final Value value) throws IOException {
        validator.validate(keyRepository.read(value.getKey()), value);
    }

    public Value read(final String key, final Map<String, String> filter) throws IOException {
        return readAllByKey(key, filter).stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

    public List<Value> readAllByKey(final String key, final Map<String, String> filter) throws IOException {
        checkKeyExists(key);

        final List<Value> values = valueRepository.findAll(byKey(key));
        final Map<Dimension, Relation> dimensions = readDimensions();

        sort(values, dimensions);

        return match(values, dimensions, filter);
    }

    public ListMultimap<String, Value> readAllByKeyPattern(@Nullable final String keyPattern) throws IOException {
        final ValueCriteria criteria = keyPattern == null ? withoutCriteria() : byKeyPattern(keyPattern);
        final List<Value> values = valueRepository.findAll(criteria);

        final Map<Dimension, Relation> dimensions = readDimensions();
        final ListMultimap<String, Value> entries = create(index(values, Value::getKey));

        entries.keySet().forEach(key ->
            sort(entries.get(key), dimensions));

        return entries;
    }

    private void checkKeyExists(final String key) {
        if (!keyRepository.exists(key)) {
            // TODO move this out of here
            throw new NotFoundException();
        }
    }

    private Map<Dimension, Relation> readDimensions() {
        return dimensionRepository.findAll().stream()
                .collect(toMap(identity(),
                        throwingFunction(dimension -> relationRepository.read(dimension.getRelation())),
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
        valueRepository.delete(new Realization(key, transformValues(filter, TextNode::new)));
    }

}
