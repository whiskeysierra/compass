package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueId;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static org.zalando.compass.domain.persistence.ValueCriteria.byDimension;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

// TODO document locking order: dimensions, keys and values all ordered by id
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

    public DimensionLock lock(final Dimension dimension) {
        final String dimensionId = dimension.getId();
        @Nullable final Dimension current = dimensionRepository.lock(dimensionId).orElse(null);
        final List<Value> values = valueRepository.lockAll(byDimension(dimensionId));
        return new DimensionLock(current, values);
    }

    public KeyLock lock(final Key key) {
        final String keyId = key.getId();
        @Nullable final Key current = keyRepository.lock(keyId).orElse(null);
        final List<Value> values = valueRepository.lockAll(byKey(keyId));
        return new KeyLock(current, values);
    }

    public ValueLock lock(final String keyId, final Value value) {
        final List<Dimension> dimensions = dimensionRepository.lockAll(value.getDimensions().keySet());
        final Key key = keyRepository.lock(keyId).orElseThrow(NotFoundException::new);
        @Nullable final Value current = valueRepository.lock(new ValueId(keyId, value.getDimensions())).orElse(null);

        return new ValueLock(dimensions, key, current);
    }

    @lombok.Value
    public static class DimensionLock {
        @Nullable Dimension dimension;
        List<Value> values;
    }

    @lombok.Value
    public static class KeyLock {
        @Nullable Key key;
        List<Value> values;
    }

    @lombok.Value
    public static class ValueLock {
        List<Dimension> dimensions;
        Key key;
        @Nullable Value value;
    }

}
