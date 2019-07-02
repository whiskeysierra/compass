package org.zalando.compass.revision.domain.logic;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.revision.domain.spi.repository.KeyRevisionRepository;
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

final class InMemoryKeyRevisionRepository implements KeyRevisionRepository {

    private final ConcurrentMap<String, List<KeyRevision>> revisions = new ConcurrentHashMap<>();

    @Override
    public void create(final KeyRevision key) {
        revisions.computeIfAbsent(key.getId(), unused -> new CopyOnWriteArrayList<>())
                .add(key);
    }

    @Override
    public List<Revision> findPageRevisions(final Pagination<Long> query) {
        return revisions.values().stream()
                .flatMap(Collection::stream)
                .map(KeyRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot().compareTo(revision.getId()) < 0)
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

    @Override
    public List<Key> findPage(final long revisionId, final Pagination<String> query) {
        return revisions.values().stream()
                .map(revisions -> revisions.stream()
                        .max(comparingLong(r -> r.getRevision().getId()))
                        .filter(r -> r.getRevision().getType() != DELETE)
                        .orElse(null))
                .filter(Objects::nonNull)
                // TODO sort order and comparison dependant on pagination direction
                .filter(r -> query.getPivot() == null || query.getPivot().compareTo(r.getId()) < 0)
                .sorted(comparing(KeyRevision::getId))
                .limit(query.getLimit())
                .map(r -> new Key(r.getId(), r.getSchema(), r.getDescription()))
                .collect(toList());
    }

    @Override
    public List<Revision> findRevisions(final String id, final Pagination<Long> query) {
        return revisions.getOrDefault(id, emptyList()).stream()
                .map(KeyRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot() < revision.getId())
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

    @Override
    public Optional<KeyRevision> find(final String id, final long revision) {
        return revisions.getOrDefault(id, emptyList()).stream()
                .filter(key -> key.getRevision().getId() == revision)
                .findFirst();
    }
}
