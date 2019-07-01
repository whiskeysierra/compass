package org.zalando.compass.domain.spi.repository.lock;

import org.zalando.compass.domain.model.Key;

import java.util.Optional;

public interface KeyLockRepository {

    Optional<Key> lock(String id);

}
