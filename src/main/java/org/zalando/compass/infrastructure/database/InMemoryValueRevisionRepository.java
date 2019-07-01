package org.zalando.compass.infrastructure.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.revision.ValueRevision;
import org.zalando.compass.domain.spi.repository.revision.ValueRevisionRepository;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.DELETE;

public final class InMemoryValueRevisionRepository implements ValueRevisionRepository {

    private final ConcurrentMap<String, List<ValueRevision>> revisions = new ConcurrentHashMap<>();

    @Override
    public void create(final String key, final ValueRevision value) {
        revisions.computeIfAbsent(key, unused -> new CopyOnWriteArrayList<>()).add(value);
    }

    @Override
    public List<Revision> findPageRevisions(final String key, final Pagination<Long> query) {
        return revisions.getOrDefault(key, emptyList()).stream()
                .map(ValueRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot().compareTo(revision.getId()) < 0)
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

    @Override
    public List<ValueRevision> findPage(final String key, final long revisionId) {
        return find(r -> r.getRevision().getType() != DELETE);
    }

    @Override
    public List<ValueRevision> findValueRevisions(final String key, final long revisionId) {
        return find(r -> true);
    }

    private List<ValueRevision> find(final Predicate<ValueRevision> predicate) {
        return revisions.values().stream()
                .map(revisions -> revisions.stream()
                        .max(comparingLong(r -> r.getRevision().getId()))
                        .filter(predicate)
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted(comparingLong(r -> r.getRevision().getId()))
                .collect(toList());
    }

    @Override
    public List<Revision> findRevisions(final String key, final Map<String, JsonNode> dimensions, final Pagination<Long> query) {
        return revisions.getOrDefault(key, emptyList()).stream()
                .map(ValueRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot() < revision.getId())
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

}
