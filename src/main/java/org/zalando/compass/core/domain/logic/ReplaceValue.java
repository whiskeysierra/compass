package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.model.event.ValueCreated;
import org.zalando.compass.core.domain.model.event.ValueReplaced;
import org.zalando.compass.core.domain.model.event.ValuesReplaced;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;
import org.zalando.compass.core.domain.spi.validation.ValidationService;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Maps.difference;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Streams.mapWithIndex;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReplaceValue {

    private final ValueLocking locking;
    private final ValidationService validator;
    private final ValueRepository repository;
    private final EventPublisher publisher;

    boolean replace(final String keyId, final Value value, @Nullable final String comment) {
        final var lock = lock(keyId, value);
        final var key = lock.getKey();
        @Nullable final var before = lock.getValue();

        if (before == null) {
            final var created = create(key, value);
            publisher.publish(new ValueReplaced(key, null, created, comment));
            return true;
        } else {
            final var after = value.withIndex(before.getIndex());
            update(key, after);
            publisher.publish(new ValueReplaced(key, before, after, comment));
            return false;
        }
    }

    void create(final String keyId, final Value value, @Nullable final String comment) {
        final var lock = lock(keyId, value);

        final var key = lock.getKey();
        @Nullable final var current = lock.getValue();

        if (current == null) {
            final var created = create(key, value);
            publisher.publish(new ValueCreated(key, created, comment));
        } else {
            final var dimensions = lock.getDimensions().stream()
                    .map(Dimension::getId)
                    .collect(joining(", "));

            throw new EntityAlreadyExistsException(
                    "Value for key " + keyId + " and dimensions [" + dimensions + "] already exists");
        }
    }

    boolean replace(final String keyId, final List<Value> values, @Nullable final String comment) {
        log.info("Replacing values of key [{}]", keyId);

        final var lock = lock(keyId, values);

        final var key = lock.getKey();
        final var before = lock.getValues();
        final var after = preserveIndex(values);

        return replace(key, before, after, comment);
    }

    boolean create(final String keyId, final List<Value> values, @Nullable final String comment) {
        log.info("Creating values of key [{}]", keyId);

        final var lock = lock(keyId, values);

        final var key = lock.getKey();
        final var before = lock.getValues();
        final var after = preserveIndex(values);

        if (before.isEmpty()) {
            return replace(key, before, after, comment);
        } else {
            throw new EntityAlreadyExistsException();
        }
    }

    private ValueLock lock(final String key, final Value value) {
        final var lock = locking.lock(key, value.getDimensions());

        validator.check(lock.getDimensions(), value);
        validator.check(lock.getKey(), value);

        return lock;
    }

    private ValuesLock lock(final String key, final List<Value> values) {
        final var lock = locking.lock(key, values);

        validator.check(lock.getDimensions(), values);
        validator.check(lock.getKey(), values);

        return lock;
    }

    private boolean replace(final Key key, final List<Value> before, final List<Value> after,
            @Nullable final String comment) {

        final var diff = difference(
                uniqueIndex(before, Value::getDimensions),
                uniqueIndex(after, Value::getDimensions));

        final var creates = diff.entriesOnlyOnRight().values();
        final var updates = diff.entriesDiffering().values();
        final var deletes = diff.entriesOnlyOnLeft().values();

        final Collection<Value> created = creates.stream()
                .map(value -> create(key, value))
                .collect(toList());

        updates.forEach(pair ->
            update(key, pair.rightValue()));

        deletes.forEach(value ->
            delete(key, value));

        publisher.publish(new ValuesReplaced(key, created, updates, deletes, comment));

        return !creates.isEmpty();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<Value> preserveIndex(final List<Value> values) {
        return mapWithIndex(values.stream(), Value::withIndex).collect(toList());
    }

    private Value create(final Key key, final Value value) {
        final var created = repository.create(key.getId(), value);
        log.info("Created value for key [{}]: [{}]", key.getId(), created);
        return created;
    }

    private void update(final Key key, final Value after) {
        repository.update(key.getId(), after);
        log.info("Updated value for key [{}]: [{}]", key.getId(), after);
    }

    private void delete(final Key key, final Value value) {
        repository.delete(key.getId(), value.getDimensions());
        log.info("Deleted value for key [{}]: [{}]", key.getId(), value);
    }

}
