package org.zalando.compass.domain.repository;

import org.zalando.compass.domain.model.Dimension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DimensionGuard {

    List<Dimension> lockAll(Set<String> dimensions);

    Optional<Dimension> lock(String id);

}
