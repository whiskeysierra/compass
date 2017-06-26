package org.zalando.compass.domain.logic.value;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Sets.difference;

@Slf4j
@Component
class ReplaceValue {

    private final Locking locking;
    private final ValidationService validator;
    private final ValueRepository repository;

    @Autowired
    ReplaceValue(final Locking locking, final ValidationService validator, final ValueRepository repository) {
        this.validator = validator;
        this.repository = repository;
        this.locking = locking;
    }

    boolean replace(final String key, final Value value) {
        final ValueLock lock = locking.lockValue(key, value.getDimensions());
        @Nullable final Value current = lock.getValue();

        validator.check(lock.getDimensions(), value);
        validator.check(lock.getKey(), value);

        // TODO make sure this is transactional
        if (current == null) {
            repository.create(key, value);
            log.info("Created value for key [{}]: [{}]", key, value);

            return true;
        } else {
            repository.update(key, value);
            log.info("Updated value for key [{}]: [{}]", key, value);

            return false;
        }
    }

    void replace(final String key, final List<Value> values) {
        log.info("Replacing values of key [{}]", key);

        final ValuesLock lock = locking.lock(key, values);

        validator.check(lock.getDimensions(), values);
        validator.check(lock.getKey(), values);

        final Set<Equivalence.Wrapper<Value>> before = wrap(lock.getValues());
        final Set<Equivalence.Wrapper<Value>> after = wrap(values);

        final Collection<Value> creations = unwrap(difference(after, before));
        final Collection<Value> deletions = unwrap(difference(before, after));

        creations.forEach(value ->
                repository.create(key, value));

        deletions.forEach(value ->
                repository.delete(key, value.getDimensions()));

        // performs updates and assigns correct index values (for order by)
        repository.update(key, values);

        log.info("Replaced values of key [{}] with [{}]", key, values);
    }

    private ImmutableSet<Equivalence.Wrapper<Value>> wrap(final Collection<Value> values) {
        final Equivalence<Value> equivalence = Equivalence.equals().onResultOf(Value::getDimensions);
        return values.stream()
                .map(equivalence::wrap)
                .collect(toImmutableSet());
    }

    private ImmutableSet<Value> unwrap(final Collection<Equivalence.Wrapper<Value>> values) {
        return values.stream()
                .map(Equivalence.Wrapper::get)
                .collect(toImmutableSet());
    }

}
