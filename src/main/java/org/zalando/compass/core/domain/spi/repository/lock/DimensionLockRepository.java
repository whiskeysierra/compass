package org.zalando.compass.core.domain.spi.repository.lock;

import org.zalando.compass.core.domain.model.Dimension;

import java.util.Optional;
import java.util.Set;

public interface DimensionLockRepository {

    Set<Dimension> lockAll(Set<Dimension> dimensions);

    Optional<Dimension> lock(Dimension dimension);

}
