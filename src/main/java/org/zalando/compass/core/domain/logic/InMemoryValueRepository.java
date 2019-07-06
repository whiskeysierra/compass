package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.Values;
import org.zalando.compass.core.domain.spi.repository.ValueCriteria;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;
import org.zalando.compass.core.domain.spi.repository.lock.ValueLockRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;

final class InMemoryValueRepository implements ValueRepository, ValueLockRepository {

    private final ConcurrentMap<String, List<Value>> values = new ConcurrentHashMap<>();

    @Override
    public Value create(final String key, final Value value) {
        final var list = this.values.computeIfAbsent(key, unused -> new CopyOnWriteArrayList<>());
        final var index = list.size();
        list.add(value);

        return value.withIndex((long) index);
    }

    @Override
    public Values findAll(final ValueCriteria criteria) {
        if (criteria.getKey() != null) {
            return new Values(ImmutableList.copyOf(values.getOrDefault(criteria.getKey(), emptyList())));
        }

        if (criteria.getDimension() != null) {
            return values.values().stream()
                    .map(Collection::stream)
                    .flatMap(Function.identity())
                    .filter(v -> v.getDimensions().containsKey(criteria.getDimension()))
                    .collect(collectingAndThen(toImmutableList(), Values::new));
        }

        return new Values();
    }

    @Override
    public void update(final String key, final Value value) {
        lock(key, value.getDimensions()).ifPresent(present ->
                values.computeIfAbsent(key, unused -> new CopyOnWriteArrayList<>())
                        // TODO index is optional!
                        .set(value.getIndex().intValue(), value));
    }

    @Override
    public void delete(final String key, final Map<Dimension, JsonNode> dimensions) {
        values.getOrDefault(key, emptyList())
                .removeIf(value -> value.getDimensions().equals(dimensions));
    }

    @Override
    public Values lockAll(final ValueCriteria criteria) {
        return findAll(criteria);
    }

    @Override
    public Optional<Value> lock(final String key, final Map<Dimension, JsonNode> dimensions) {
        return values.getOrDefault(key, emptyList()).stream()
                .filter(value -> value.getDimensions().equals(dimensions))
                .findFirst();
    }

}
