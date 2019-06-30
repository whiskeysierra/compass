package org.zalando.compass.domain.repository;

import org.zalando.compass.domain.model.Key;

import java.util.Optional;

public interface KeyGuard {

    Optional<Key> lock(String id);

}
