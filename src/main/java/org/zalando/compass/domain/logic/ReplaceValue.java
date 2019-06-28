package org.zalando.compass.domain.logic;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.EntityAlreadyExistsException;
import org.zalando.compass.domain.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.repository.ValueRepository;
import org.zalando.compass.domain.repository.ValueRevisionRepository;
import org.zalando.compass.infrastructure.database.model.enums.RevisionType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Streams.mapWithIndex;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.CREATE;
import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.DELETE;
import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.UPDATE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReplaceValue {

    private final Locking locking;
    private final ValidationService validator;
    private final ValueRepository repository;
    private final RevisionService revisionService;
    private final ValueRevisionRepository revisionRepository;

    boolean replace(final String key, final Value value, @Nullable final String comment) {
        final ValueLock lock = lock(key, value);
        @Nullable final Value current = lock.getValue();

        final Revision rev = revisionService.create(comment);

        if (current == null) {
            create(key, value, rev);
            return true;
        } else {
            update(key, value.withIndex(current.getIndex()), rev);
            return false;
        }
    }

    void create(final String key, final Value value, @Nullable final String comment) {
        final ValueLock lock = lock(key, value);
        @Nullable final Value current = lock.getValue();

        final Revision rev = revisionService.create(comment);

        if (current == null) {
            create(key, value, rev);
        } else {
            final String dimensions = lock.getDimensions().stream()
                    .map(Dimension::getId)
                    .collect(joining(", "));

            throw new EntityAlreadyExistsException(
                    "Value for key " + key + " and dimensions [" + dimensions + "] already exists");
        }
    }

    boolean replace(final String key, final List<Value> values, @Nullable final String comment) {
        log.info("Replacing values of key [{}]", key);

        final ValuesLock lock = lock(key, values);

        final List<Value> before = lock.getValues();
        final List<Value> after = preserveIndex(values);

        return replace(key, before, after, comment);
    }

    boolean create(final String key, final List<Value> values, @Nullable final String comment) {
        log.info("Creating values of key [{}]", key);

        final ValuesLock lock = lock(key, values);

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

    private boolean replace(final String key, final List<Value> left, final List<Value> right,
            @Nullable final String comment) {
        final Set<Wrapper<Value>> before = wrap(left);
        final Set<Wrapper<Value>> after = wrap(right);

        final Collection<Value> creates = unwrap(difference(after, before));
        final Collection<Value> updates = unwrap(intersection(after, before));
        final Collection<Value> deletes = unwrap(difference(before, after));

        final Revision revision = revisionService.create(comment);

        creates.forEach(value ->
            create(key, value, revision));

        updates.forEach(value ->
            update(key, value, revision));

        deletes.forEach(value ->
            delete(key, value, revision));

        return !creates.isEmpty();
    }

    private static List<Value> preserveIndex(final List<Value> values) {
        return mapWithIndex(values.stream(), Value::withIndex).collect(toList());
    }

    private static Set<Wrapper<Value>> wrap(final Collection<Value> values) {
        final Equivalence<Value> equivalence = Equivalence.equals().onResultOf(Value::getDimensions);
        return values.stream().map(equivalence::wrap).collect(toSet());
    }

    private static <T> Collection<T> unwrap(final Collection<Wrapper<T>> wraps) {
        return wraps.stream().map(Equivalence.Wrapper::get).collect(toList());
    }

    private void create(final String key, final Value value, final Revision rev) {
        final Value created = repository.create(key, value);
        log.info("Created value for key [{}]: [{}]", key, created);
        createRevision(key, created, rev, CREATE);
    }

    private void update(final String key, final Value value, final Revision rev) {
        repository.update(key, value);
        log.info("Updated value for key [{}]: [{}]", key, value);
        createRevision(key, value, rev, UPDATE);
    }

    private void delete(final String key, final Value value, final Revision rev) {
        repository.delete(key, value.getDimensions());
        log.info("Deleted value for key [{}]: [{}]", key, value);
        createRevision(key, value, rev, DELETE);
    }

    private void createRevision(final String key, final Value value, final Revision revision, final RevisionType type) {
        final ValueRevision valueRevision = value.toRevision(revision.withType(type));
        revisionRepository.create(key, valueRevision);
        log.info("Created value revision [{}]", valueRevision);
    }

}
