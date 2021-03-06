package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.core.domain.spi.repository.lock.DimensionLockRepository;
import org.zalando.compass.core.domain.spi.repository.lock.KeyLockRepository;
import org.zalando.compass.core.domain.spi.repository.lock.ValueLockRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.zalando.compass.core.domain.api.NotFoundException.exists;
import static org.zalando.compass.core.domain.spi.repository.ValueCriteria.byKey;

/**
 * TODO this is actually a postgres/database concern and shouldn't be here:
 *
 * Centralizes locking of entities. In order to avoid deadlocks we ensure the following order of locks:
 *
 * <ol>
 *     <li>Dimensions</li>
 *     <li>Keys</li>
 *     <li>Values</li>
 * </ol>
 *
 * All ordered by id, respectively.
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ValueLocking {

    private final DimensionLockRepository dimensionLockRepository;
    private final KeyLockRepository keyLockRepository;
    private final ValueLockRepository valueLockRepository;

    ValueLock lock(final String keyId, final Map<Dimension, JsonNode> filter) {
        final var dimensions = lockDimensions(filter.keySet());
        final var key = keyLockRepository.lock(keyId).orElseThrow(NotFoundException::new);
        @Nullable final var current = valueLockRepository.lock(keyId, filter).orElse(null);

        return new ValueLock(dimensions, key, current);
    }

    ValuesLock lock(final String keyId, final List<Value> values) {
        final var dimensions = lockDimensions(values.stream()
                .flatMap(value -> value.getDimensions().keySet().stream())
                .collect(toSet()));
        final var key = keyLockRepository.lock(keyId).orElseThrow(NotFoundException::new);
        final List<Value> current = valueLockRepository.lockAll(byKey(keyId));

        return new ValuesLock(dimensions, key, current);
    }

    private Set<Dimension> lockDimensions(final Set<Dimension> identifiers) {
        final var dimensions = dimensionLockRepository.lockAll(identifiers);
        final Set<Dimension> difference = Sets.difference(identifiers, dimensions);

        exists(difference.isEmpty(), "Dimensions: %s", difference);

        return dimensions;
    }

}
