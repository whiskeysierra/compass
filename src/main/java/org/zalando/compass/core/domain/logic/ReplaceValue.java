package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.spi.repository.ValueRepository;
import org.zalando.compass.core.domain.spi.validation.ValidationService;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.kernel.domain.model.event.ValueCreated;
import org.zalando.compass.kernel.domain.model.event.ValueReplaced;
import org.zalando.compass.kernel.domain.model.event.ValuesReplaced;

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
    private final ApplicationEventPublisher publisher;

    boolean replace(final String keyId, final Value value, @Nullable final String comment) {
        final ValueLock lock = lock(keyId, value);
        final Key key = lock.getKey();
        @Nullable final Value before = lock.getValue();

        if (before == null) {
            final Value created = create(key, value);
            publisher.publishEvent(new ValueCreated(key, created, comment));
            return true;
        } else {
            final Value after = value.withIndex(before.getIndex());
            update(key, after);
            publisher.publishEvent(new ValueReplaced(key, before, after, comment));
            return false;
        }
    }

    void create(final String keyId, final Value value, @Nullable final String comment) {
        final ValueLock lock = lock(keyId, value);

        final Key key = lock.getKey();
        @Nullable final Value current = lock.getValue();

        if (current == null) {
            final Value created = create(key, value);
            publisher.publishEvent(new ValueCreated(key, created, comment));
        } else {
            final String dimensions = lock.getDimensions().stream()
                    .map(Dimension::getId)
                    .collect(joining(", "));

            throw new EntityAlreadyExistsException(
                    "Value for key " + keyId + " and dimensions [" + dimensions + "] already exists");
        }
    }

    boolean replace(final String keyId, final List<Value> values, @Nullable final String comment) {
        log.info("Replacing values of key [{}]", keyId);

        final ValuesLock lock = lock(keyId, values);

        final Key key = lock.getKey();
        final List<Value> before = lock.getValues();
        final List<Value> after = preserveIndex(values);

        return replace(key, before, after, comment);
    }

    boolean create(final String keyId, final List<Value> values, @Nullable final String comment) {
        log.info("Creating values of key [{}]", keyId);

        final ValuesLock lock = lock(keyId, values);

        final Key key = lock.getKey();
        final List<Value> before = lock.getValues();
        final List<Value> after = preserveIndex(values);

        if (before.isEmpty()) {
            return replace(key, before, after, comment);
        } else {
            throw new EntityAlreadyExistsException();
        }
    }

    private ValueLock lock(final String key, final Value value) {
        final ValueLock lock = locking.lock(key, value.getDimensions());

        validator.check(lock.getDimensions(), value);
        validator.check(lock.getKey(), value);

        return lock;
    }

    private ValuesLock lock(final String key, final List<Value> values) {
        final ValuesLock lock = locking.lock(key, values);

        validator.check(lock.getDimensions(), values);
        validator.check(lock.getKey(), values);

        return lock;
    }

    private boolean replace(final Key key, final List<Value> before, final List<Value> after,
            @Nullable final String comment) {

        final MapDifference<ImmutableMap<String, JsonNode>, Value> diff = difference(
                uniqueIndex(before, Value::getDimensions),
                uniqueIndex(after, Value::getDimensions));

        final Collection<Value> creates = diff.entriesOnlyOnRight().values();
        final Collection<ValueDifference<Value>> updates = diff.entriesDiffering().values();
        final Collection<Value> deletes = diff.entriesOnlyOnLeft().values();

        final Collection<Value> created = creates.stream()
                .map(value -> create(key, value))
                .collect(toList());

        updates.forEach(pair ->
            update(key, pair.rightValue()));

        deletes.forEach(value ->
            delete(key, value));

        publisher.publishEvent(new ValuesReplaced(key, created, updates, deletes, comment));

        return !creates.isEmpty();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<Value> preserveIndex(final List<Value> values) {
        return mapWithIndex(values.stream(), Value::withIndex).collect(toList());
    }

    private Value create(final Key key, final Value value) {
        final Value created = repository.create(key.getId(), value);
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
