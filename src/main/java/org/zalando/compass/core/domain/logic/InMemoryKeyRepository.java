package org.zalando.compass.core.domain.logic;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.spi.repository.KeyRepository;
import org.zalando.compass.core.domain.spi.repository.lock.KeyLockRepository;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;

final class InMemoryKeyRepository implements KeyRepository, KeyLockRepository {

    private final ConcurrentMap<String, Key> keys = new ConcurrentHashMap<>();

    @Override
    public void create(final Key key) {
        keys.put(key.getId(), key);
    }

    @Override
    public List<Key> findAll(@Nullable final String term, final Pagination<String> query) {
        return keys.values().stream()
                .filter(key -> term == null || (
                        key.getId().toLowerCase(ROOT).contains(term.toLowerCase(ROOT)) ||
                        key.getDescription().toLowerCase(ROOT).contains(term.toLowerCase(ROOT))))
                .collect(toList());
    }

    @Override
    public Optional<Key> find(final String id) {
        return Optional.ofNullable(keys.get(id));
    }

    @Override
    public void update(final Key key) {
        keys.replace(key.getId(), key);
    }

    @Override
    public void delete(final Key key) {
        keys.remove(key.getId());
    }

    @Override
    public Optional<Key> lock(final String id) {
        return find(id);
    }
}
