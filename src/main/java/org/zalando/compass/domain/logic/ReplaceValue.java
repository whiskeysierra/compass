package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.EntityAlreadyExistsException;
import org.zalando.compass.domain.ValidationService;
import org.zalando.compass.domain.event.ValueCreated;
import org.zalando.compass.domain.event.ValueDeleted;
import org.zalando.compass.domain.event.ValueReplaced;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.repository.ValueRepository;

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

    private final Locking locking;
    private final ValidationService validator;
    private final ValueRepository repository;
    private final RevisionService revisionService;
    private final ApplicationEventPublisher publisher;

    boolean replace(final String keyId, final Value value, @Nullable final String comment) {
        final ValueLock lock = lock(keyId, value);
        final Key key = lock.getKey();
        @Nullable final Value current = lock.getValue();

        final Revision rev = revisionService.create(comment);

        if (current == null) {
            create(key, value, rev);
            return true;
        } else {
            update(key, current, value.withIndex(current.getIndex()), rev);
            return false;
        }
    }

    void create(final String keyId, final Value value, @Nullable final String comment) {
        final ValueLock lock = lock(keyId, value);

        final Key key = lock.getKey();
        @Nullable final Value current = lock.getValue();

        final Revision rev = revisionService.create(comment);

        if (current == null) {
            create(key, value, rev);
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
        final ValueLock lock = locking.lockValue(key, value.getDimensions());

        validator.check(lock.getDimensions(), value);
        validator.check(lock.getKey(), value);

        return lock;
    }

    private ValuesLock lock(final String key, final List<Value> values) {
        final ValuesLock lock = locking.lockValues(key, values);

        validator.check(lock.getDimensions(), values);
        validator.check(lock.getKey(), values);

        return lock;
    }

    private boolean replace(final Key key, final List<Value> before, final List<Value> after,
            @Nullable final String comment) {

        final MapDifference<ImmutableMap<String, JsonNode>, Value> difference = difference(
                uniqueIndex(before, Value::getDimensions),
                uniqueIndex(after, Value::getDimensions));

        final Collection<Value> creates = difference.entriesOnlyOnRight().values();
        final Collection<ValueDifference<Value>> updates = difference.entriesDiffering().values();
        final Collection<Value> deletes = difference.entriesOnlyOnLeft().values();

        final Revision revision = revisionService.create(comment);

        creates.forEach(value ->
            create(key, value, revision));

        updates.forEach(pair ->
            update(key, pair.leftValue(), pair.rightValue(), revision));

        deletes.forEach(value ->
            delete(key, value, revision));

        return !creates.isEmpty();
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<Value> preserveIndex(final List<Value> values) {
        return mapWithIndex(values.stream(), Value::withIndex).collect(toList());
    }

    private void create(final Key key, final Value value, final Revision rev) {
        final Value created = repository.create(key.getId(), value);
        log.info("Created value for key [{}]: [{}]", key.getId(), created);
        publisher.publishEvent(new ValueCreated(key, created, rev));
    }

    private void update(final Key key, final Value before, final Value after, final Revision rev) {
        repository.update(key.getId(), after);
        log.info("Updated value for key [{}]: [{}]", key.getId(), after);
        publisher.publishEvent(new ValueReplaced(key, before, after, rev));
    }

    private void delete(final Key key, final Value value, final Revision rev) {
        repository.delete(key.getId(), value.getDimensions());
        log.info("Deleted value for key [{}]: [{}]", key.getId(), value);
        publisher.publishEvent(new ValueDeleted(key, value, rev));
    }

}
