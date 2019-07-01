package org.zalando.compass.core.domain.spi.repository.lock;

import org.zalando.compass.kernel.domain.model.Dimension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DimensionLockRepository {

    List<Dimension> lockAll(Set<String> dimensions);

    Optional<Dimension> lock(String id);

}
