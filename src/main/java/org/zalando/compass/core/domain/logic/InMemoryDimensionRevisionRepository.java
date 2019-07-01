package org.zalando.compass.core.domain.logic;

import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;
import org.zalando.compass.library.pagination.Pagination;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;

public final class InMemoryDimensionRevisionRepository implements DimensionRevisionRepository {

    private final ConcurrentMap<String, List<DimensionRevision>> revisions = new ConcurrentHashMap<>();

    @Override
    public void create(final DimensionRevision dimension) {
        revisions.computeIfAbsent(dimension.getId(), unused -> new CopyOnWriteArrayList<>())
                .add(dimension);
    }

    @Override
    public List<Revision> findPageRevisions(final Pagination<Long> query) {
        return revisions.values().stream()
                .flatMap(Collection::stream)
                .map(DimensionRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot().compareTo(revision.getId()) < 0)
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

    @Override
    public List<Dimension> findPage(final long revisionId, final Pagination<String> query) {
        return revisions.values().stream()
                .map(revisions -> revisions.stream()
                        .max(comparingLong(r -> r.getRevision().getId()))
                        .filter(r -> r.getRevision().getType() != DELETE)
                        .orElse(null))
                .filter(Objects::nonNull)
                // TODO sort order and comparison dependant on pagination direction
                .filter(r -> query.getPivot() == null || query.getPivot().compareTo(r.getId()) < 0)
                .sorted(comparing(DimensionRevision::getId))
                .limit(query.getLimit())
                .map(r -> new Dimension(r.getId(), r.getSchema(), r.getRelation(), r.getDescription()))
                .collect(toList());
    }

    @Override
    public List<Revision> findRevisions(final String id, final Pagination<Long> query) {
        return revisions.getOrDefault(id, emptyList()).stream()
                .map(DimensionRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot() < revision.getId())
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

    @Override
    public Optional<DimensionRevision> find(final String id, final long revision) {
        return revisions.getOrDefault(id, emptyList()).stream()
                .filter(dimension -> dimension.getRevision().getId() == revision)
                .findFirst();
    }
}
