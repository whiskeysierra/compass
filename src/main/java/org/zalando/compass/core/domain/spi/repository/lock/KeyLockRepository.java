package org.zalando.compass.core.domain.spi.repository.lock;

import org.zalando.compass.kernel.domain.model.Key;

import java.util.Optional;

public interface KeyLockRepository {

    Optional<Key> lock(String id);

}
