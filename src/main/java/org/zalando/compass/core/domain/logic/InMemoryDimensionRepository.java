package org.zalando.compass.core.domain.logic;

import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.core.domain.spi.repository.lock.DimensionLockRepository;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toSet;

final class InMemoryDimensionRepository implements DimensionRepository, DimensionLockRepository {

    private final ConcurrentMap<String, Dimension> dimensions = new ConcurrentHashMap<>();

    @Override
    public void create(final Dimension dimension) {
        dimensions.put(dimension.getId(), dimension);
    }

    @Override
    public Set<Dimension> findAll(final Set<String> dimensions) {
        return dimensions.stream()
                .map(this::find)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toImmutableSet());
    }

    @Override
    public Set<Dimension> findAll(@Nullable final String term, final Pagination<String> query) {
        return dimensions.values().stream()
                .filter(dimension -> term == null || (
                        dimension.getId().toLowerCase(ROOT).contains(term.toLowerCase(ROOT)) ||
                        dimension.getDescription().toLowerCase(ROOT).contains(term.toLowerCase(ROOT))))
                .collect(toImmutableSet());
    }

    @Override
    public Optional<Dimension> find(final String id) {
        return Optional.ofNullable(dimensions.get(id));
    }

    @Override
    public void update(final Dimension dimension) {
        dimensions.replace(dimension.getId(), dimension);
    }

    @Override
    public void delete(final Dimension dimension) {
        dimensions.remove(dimension.getId());
    }

    // TODO do we need return values for lock methods?
    @Override
    public Set<Dimension> lockAll(final Set<Dimension> dimensions) {
        return findAll(dimensions.stream().map(Dimension::getId).collect(toSet()));
    }

    @Override
    public Optional<Dimension> lock(final Dimension dimension) {
        return find(dimension.getId());
    }
}
