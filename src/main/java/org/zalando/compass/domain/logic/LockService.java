package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import static org.zalando.compass.domain.persistence.ValueCriteria.byDimension;
import static org.zalando.compass.domain.persistence.ValueCriteria.byKey;

// TODO think about which version of the entity we need to look at, the current or the next?
@Component
public class LockService {

    private final DimensionRepository dimensionRepository;
    private final KeyRepository keyRepository;
    private final ValueRepository valueRepository;

    @Autowired
    LockService(
            final DimensionRepository dimensionRepository,
            final KeyRepository keyRepository,
            final ValueRepository valueRepository) {
        this.dimensionRepository = dimensionRepository;
        this.keyRepository = keyRepository;
        this.valueRepository = valueRepository;
    }

    public void onUpdate(final Dimension dimension) {
        valueRepository.lockAll(byDimension(dimension.getId()));
    }

    public void onUpdate(final Key key) {
        valueRepository.lockAll(byKey(key.getId()));
    }

    public void onReplace(final String key, final Value value) {
        dimensionRepository.lockAll(value.getDimensions().keySet());
        keyRepository.lock(key);
    }

}
