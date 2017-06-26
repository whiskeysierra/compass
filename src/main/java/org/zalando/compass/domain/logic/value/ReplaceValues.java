package org.zalando.compass.domain.logic.value;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Sets.difference;

@Slf4j
@Component
class ReplaceValues {

    private final ValueRepository repository;
    private final ValidationService validator;
    private final Locking locking;

    @Autowired
    ReplaceValues(
            final ValueRepository repository,
            final ValidationService validator,
            final Locking locking) {
        this.repository = repository;
        this.validator = validator;
        this.locking = locking;
    }

    @Transactional
    public void replace(final String key, final List<Value> values) {
        log.info("Replacing values of key [{}]", key);

        final ValuesLock lock = locking.lock(key, values);

        validator.validate(lock.getDimensions(), values);
        validator.validate(lock.getKey(), values);

        final Set<Wrapper<Value>> before = wrap(lock.getValues());
        final Set<Wrapper<Value>> after = wrap(values);

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

    private ImmutableSet<Wrapper<Value>> wrap(final Collection<Value> values) {
        final Equivalence<Value> equivalence = Equivalence.equals().onResultOf(Value::getDimensions);
        return values.stream()
                .map(equivalence::wrap)
                .collect(toImmutableSet());
    }

    private ImmutableSet<Value> unwrap(final Collection<Wrapper<Value>> values) {
        return values.stream()
                .map(Wrapper::get)
                .collect(toImmutableSet());
    }

}
