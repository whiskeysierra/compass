package org.zalando.compass.domain.logic.value;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Revision.Type;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.persistence.ValueRepository;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;
import org.zalando.compass.library.Maps.Pair;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Streams.mapWithIndex;
import static java.util.stream.Collectors.toList;
import static org.zalando.compass.domain.model.Revision.Type.CREATE;
import static org.zalando.compass.domain.model.Revision.Type.DELETE;
import static org.zalando.compass.domain.model.Revision.Type.UPDATE;
import static org.zalando.compass.library.Maps.diff;

@Slf4j
@Component
class ReplaceValue {

    private final Locking locking;
    private final ValidationService validator;
    private final ValueRepository repository;
    private final RevisionService revisionService;
    private final ValueRevisionRepository revisionRepository;

    @Autowired
    ReplaceValue(final Locking locking, final ValidationService validator, final ValueRepository repository,
            final RevisionService revisionService, final ValueRevisionRepository revisionRepository) {
        this.validator = validator;
        this.repository = repository;
        this.locking = locking;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
    }

    boolean replace(final String key, final Value value) {
        final ValueLock lock = locking.lockValue(key, value.getDimensions());
        @Nullable final Value current = lock.getValue();

        // TODO validate that all mentioned dimensions and key exists
        validator.check(lock.getDimensions(), value);
        validator.check(lock.getKey(), value);

        // TODO expect comment
        final String comment = "..";
        final Revision rev = revisionService.create(comment);

        // TODO make sure this is transactional
        if (current == null) {
            create(key, value, rev);
            return true;
        } else {
            update(key, value.withIndex(current.getIndex()), rev);
            return false;
        }
    }

    void replace(final String key, final List<Value> values) {
        log.info("Replacing values of key [{}]", key);

        final ValuesLock lock = locking.lock(key, values);

        // TODO combine both into one set of violations
        validator.check(lock.getDimensions(), values);
        validator.check(lock.getKey(), values);

        final List<Value> before = lock.getValues();
        final List<Value> after = preserveIndex(values);

        final Collection<Pair<Value, Value>> diff = diff(before, after, Value::getDimensions).values();

        // TODO expect comment
        final String comment = "..";
        final Revision revision = revisionService.create(comment);

        diff.forEach(pair -> {
            final Value current = pair.getLeft();
            final Value next = pair.getRight();

            if (current == null) {
                create(key, next, revision);
            } else if (next == null) {
                delete(key, revision, current);
            } else {
                update(key, next, revision);
            }
        });

        log.info("Replaced values of key [{}] with [{}]", key, after);
    }

    private List<Value> preserveIndex(final List<Value> values) {
        return mapWithIndex(values.stream(), Value::withIndex).collect(toList());
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

    private void delete(final String key, final Revision rev, final Value value) {
        repository.delete(key, value.getDimensions());
        log.info("Deleted value for key [{}]: [{}]", key, value);
        createRevision(key, value, rev, DELETE);
    }

    private void createRevision(final String key, final Value value, final Revision revision, final Type type) {
        final ValueRevision valueRevision = value.toRevision(revision.withType(type));
        revisionRepository.create(key, valueRevision);
        log.info("Created value revision [{}]", valueRevision);
    }

}
