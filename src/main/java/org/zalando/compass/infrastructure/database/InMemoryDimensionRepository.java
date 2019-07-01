package org.zalando.compass.infrastructure.database;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.spi.repository.DimensionRepository;
import org.zalando.compass.domain.spi.repository.lock.DimensionLockRepository;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;

// TODO find a good way to a) hide this and b) make it available
public final class InMemoryDimensionRepository implements DimensionRepository, DimensionLockRepository {

    private final ConcurrentMap<String, Dimension> dimensions = new ConcurrentHashMap<>();

    @Override
    public void create(final Dimension dimension) {
        dimensions.put(dimension.getId(), dimension);
    }

    @Override
    public List<Dimension> findAll(final Set<String> dimensions) {
        return dimensions.stream()
                .map(this::find)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    @Override
    public List<Dimension> findAll(@Nullable final String term, final Pagination<String> query) {
        return dimensions.values().stream()
                .filter(dimension -> term == null || (
                        dimension.getId().toLowerCase(ROOT).contains(term.toLowerCase(ROOT)) ||
                        dimension.getDescription().toLowerCase(ROOT).contains(term.toLowerCase(ROOT))))
                .collect(toList());
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

    @Override
    public List<Dimension> lockAll(final Set<String> dimensions) {
        return findAll(dimensions);
    }

    @Override
    public Optional<Dimension> lock(final String id) {
        return find(id);
    }
}
