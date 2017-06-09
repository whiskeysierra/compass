package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueId;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueCriteria;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Sets.difference;
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

    private final ValidationService validator;
    private final RelationRepository relationRepository;
    private final DimensionRepository dimensionRepository;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    @Autowired
    public ValueService(final ValidationService validator, final RelationRepository relationRepository,
            final DimensionRepository dimensionRepository,
            final KeyRepository keyRepository, final ValueRepository valueRepository) {
        this.validator = validator;
        this.relationRepository = relationRepository;
        this.dimensionRepository = dimensionRepository;
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
    }

    public boolean create(final Value value) throws IOException {
        validateDimensions(value);
        validateValue(value);

        return valueRepository.create(value);
    }

    private void validateDimensions(final Value value) throws IOException {
        final ImmutableSet<String> dimensions = value.getDimensions().keySet();
        final List<Dimension> read = dimensionRepository.findAll(dimensions(dimensions));

        final Set<String> difference = difference(dimensions, read.stream().map(Dimension::getId).collect(toSet()));
        checkArgument(difference.isEmpty(), "Unknown dimensions: " + difference);

        validator.validate(read, value);
    }

    private void validateValue(final Value value) throws IOException {
        final Key row = keyRepository.read(value.getKey());
        validator.validate(row, value);
    }

    public Value read(final String key, final Map<String, JsonNode> filter) throws IOException {
        // TODO can't reuse readAllByKey
        checkKeyExists(key);

        final List<Value> values = valueRepository.findAll(byKey(key));
        final Map<Dimension, Relation> dimensions = readDimensions();

        final List<Value> matched = values.stream()
                .filter(dimensions.entrySet().stream()
                        .map(entry -> match(filter, entry))
                        .reduce(Predicate::and).orElse(v -> true))
                .collect(toList());

        return matched.stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

    public List<Value> readAllByKey(final String key, final Map<String, JsonNode> filter) throws IOException {
        checkKeyExists(key);

        final List<Value> values = valueRepository.findAll(byKey(key));
        final Map<Dimension, Relation> dimensions = readDimensions();

        return values.stream()
                .filter(dimensions.entrySet().stream()
                        .map(entry -> !filter.isEmpty() ? match(filter, entry) : (Predicate<Value>) ($ -> true))
                        .reduce(Predicate::and).orElse(v -> true))
                .collect(toList());
    }

    public ListMultimap<String, Value> readAllByKeyPattern(@Nullable final String keyPattern) throws IOException {
        final ValueCriteria criteria = keyPattern == null ? withoutCriteria() : byKeyPattern(keyPattern);
        final List<Value> values = valueRepository.findAll(criteria);

        return ArrayListMultimap.create(index(values, Value::getKey));
    }

    private void checkKeyExists(final String key) {
        if (!keyRepository.exists(key)) {
            // TODO move this out of here
            throw new NotFoundException();
        }
    }

    private Map<Dimension, Relation> readDimensions() {
        return dimensionRepository.findAll().stream()
                .map(row -> new Dimension(row.getId(), row.getSchema(), row.getRelation(), row.getDescription()))
                .collect(toMap(identity(),
                        throwingFunction(dimension -> relationRepository.read(dimension.getRelation())),
                        this::denyDuplicates, LinkedHashMap::new));
    }

    private <T> T denyDuplicates(final T u, @SuppressWarnings("unused") final T v) {
        throw new IllegalStateException(String.format("Duplicate key %s", u));
    }

    private Predicate<Value> match(final Map<String, JsonNode> filter, final Entry<Dimension, Relation> entry) {
        final Dimension dimension = entry.getKey();
        final Relation relation = entry.getValue();

        return value -> {
            @Nullable final JsonNode configured = value.getDimensions().get(dimension.getId());
            @Nullable final JsonNode requested = filter.get(dimension.getId());

            // TODO break this up and make it more readable
            return configured == null
                    || requested != null && !requested.isNull() && relation.test(configured, requested);
        };
    }

    public void delete(final String key, final Map<String, JsonNode> filter) throws IOException {
        valueRepository.delete(new ValueId(key, filter));
    }

}
