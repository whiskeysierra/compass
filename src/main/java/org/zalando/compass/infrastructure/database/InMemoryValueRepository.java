package org.zalando.compass.infrastructure.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.spi.repository.ValueCriteria;
import org.zalando.compass.domain.spi.repository.ValueRepository;
import org.zalando.compass.domain.spi.repository.lock.ValueLockRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public final class InMemoryValueRepository implements ValueRepository, ValueLockRepository {

    private final ConcurrentMap<String, List<Value>> values = new ConcurrentHashMap<>();

    @Override
    public Value create(final String key, final Value value) {
        final List<Value> list = this.values.computeIfAbsent(key, unused -> new CopyOnWriteArrayList<>());
        final int index = list.size();
        list.add(value);

        return value.withIndex((long) index);
    }

    @Override
    public List<Value> findAll(final ValueCriteria criteria) {
        if (criteria.getKey() != null) {
            return values.getOrDefault(criteria.getKey(), emptyList());
        }

        if (criteria.getDimension() != null) {
            return values.values().stream()
                    .map(Collection::stream)
                    .flatMap(Function.identity())
                    .filter(v -> v.getDimensions().containsKey(criteria.getDimension()))
                    .collect(toList());
        }

        return emptyList();
    }

    @Override
    public void update(final String key, final Value value) {
        lock(key, value.getDimensions()).ifPresent(present -> {
            final List<Value> values = this.values.getOrDefault(key, emptyList());
            values.set(values.indexOf(present), value);
        });
    }

    @Override
    public void delete(final String key, final Map<String, JsonNode> dimensions) {
        values.getOrDefault(key, emptyList())
                .removeIf(value -> value.getDimensions().equals(dimensions));
    }

    @Override
    public List<Value> lockAll(final ValueCriteria criteria) {
        return findAll(criteria);
    }

    @Override
    public Optional<Value> lock(final String key, final Map<String, JsonNode> dimensions) {
        return values.getOrDefault(key, emptyList()).stream()
                .filter(value -> value.getDimensions().equals(dimensions))
                .findFirst();
    }

}
