package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValuesLock;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toSet;
import static org.zalando.compass.domain.persistence.ValueCriteria.byDimension;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

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
public class Locking {

    private final DimensionRepository dimensionRepository;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    @Autowired
    Locking(
            final DimensionRepository dimensionRepository,
            final KeyRepository keyRepository,
            final ValueRepository valueRepository) {
        this.dimensionRepository = dimensionRepository;
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
    }

    public DimensionLock lockDimensions(final String id) {
        @Nullable final Dimension current = dimensionRepository.lock(id).orElse(null);
        final List<Value> values = valueRepository.lockAll(byDimension(id));
        return new DimensionLock(current, values);
    }

    public KeyLock lockKey(final String id) {
        @Nullable final Key current = keyRepository.lock(id).orElse(null);
        final List<Value> values = valueRepository.lockAll(byKey(id));
        return new KeyLock(current, values);
    }

    public ValueLock lockValue(final String keyId, final Map<String, JsonNode> filter) {
        final List<Dimension> dimensions = dimensionRepository.lockAll(filter.keySet());
        final Key key = keyRepository.lock(keyId).orElseThrow(NotFoundException::new);
        @Nullable final Value current = valueRepository.lock(keyId, filter).orElse(null);

        return new ValueLock(dimensions, key, current);
    }

    public ValuesLock lock(final String keyId, final List<Value> values) {
        final List<Dimension> dimensions = dimensionRepository.lockAll(values.stream()
                .flatMap(value -> value.getDimensions().keySet().stream())
                .collect(toSet()));
        final Key key = keyRepository.lock(keyId).orElseThrow(NotFoundException::new);
        final List<Value> current = valueRepository.lockAll(byKey(keyId));

        return new ValuesLock(dimensions, key, current);
    }

}
