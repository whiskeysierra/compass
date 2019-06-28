package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.repository.DimensionRepository;
import org.zalando.compass.domain.repository.KeyRepository;
import org.zalando.compass.domain.NotFoundException;
import org.zalando.compass.domain.repository.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.zalando.compass.domain.NotFoundException.exists;
import static org.zalando.compass.domain.repository.ValueCriteria.byDimension;
import static org.zalando.compass.domain.repository.ValueCriteria.byKey;

/**
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
class Locking {

    private final DimensionRepository dimensionRepository;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    DimensionLock lockDimension(final String id) {
        @Nullable final Dimension current = dimensionRepository.lock(id).orElse(null);
        final List<Value> values = valueRepository.lockAll(byDimension(id));

        return new DimensionLock(current, values);
    }

    KeyLock lockKey(final String id) {
        @Nullable final Key current = keyRepository.lock(id).orElse(null);
        final List<Value> values = valueRepository.lockAll(byKey(id));

        return new KeyLock(current, values);
    }

    ValueLock lockValue(final String keyId, final Map<String, JsonNode> filter) {
        final List<Dimension> dimensions = lockDimensions(filter.keySet());
        final Key key = keyRepository.lock(keyId).orElseThrow(NotFoundException::new);
        @Nullable final Value current = valueRepository.lock(keyId, filter).orElse(null);

        return new ValueLock(dimensions, key, current);
    }

    ValuesLock lockValues(final String keyId, final List<Value> values) {
        final List<Dimension> dimensions = lockDimensions(values.stream()
                .flatMap(value -> value.getDimensions().keySet().stream())
                .collect(toSet()));
        final Key key = keyRepository.lock(keyId).orElseThrow(NotFoundException::new);
        final List<Value> current = valueRepository.lockAll(byKey(keyId));

        return new ValuesLock(dimensions, key, current);
    }

    private List<Dimension> lockDimensions(final Set<String> identifiers) {
        final List<Dimension> dimensions = dimensionRepository.lockAll(identifiers);

        final Set<String> difference = Sets.difference(identifiers, dimensions.stream()
                .map(Dimension::getId)
                .collect(toSet()));

        exists(difference.isEmpty(), "Dimensions: %s", difference);

        return dimensions;
    }

}
