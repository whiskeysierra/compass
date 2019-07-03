package org.zalando.compass.revision.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.revision.domain.model.ValueRevisions;
import org.zalando.compass.revision.domain.spi.repository.ValueRevisionRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.zalando.compass.core.infrastructure.database.model.enums.RevisionType.DELETE;

final class InMemoryValueRevisionRepository implements ValueRevisionRepository {

    // TODO should this be by Key (remember to change equals/hashCode then to only check id)
    private final ConcurrentMap<String, List<ValueRevision>> revisions = new ConcurrentHashMap<>();

    @Override
    public void create(final Key key, final ValueRevision value) {
        revisions.computeIfAbsent(key.getId(), unused -> new CopyOnWriteArrayList<>()).add(value);
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
    public ValueRevisions findPage(final String key, final long revisionId) {
        return find(r -> r.getRevision().getType() != DELETE);
    }

    @Override
    public ValueRevisions findValueRevisions(final String key, final long revisionId) {
        return find(r -> true);
    }

    private ValueRevisions find(final Predicate<ValueRevision> predicate) {
        return revisions.values().stream()
                .map(revisions -> revisions.stream()
                        .max(comparingLong(r -> r.getRevision().getId()))
                        .filter(predicate)
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted(comparingLong(r -> r.getRevision().getId()))
                .collect(collectingAndThen(toImmutableList(), ValueRevisions::new));
    }

    @Override
    public List<Revision> findRevisions(final String key, @Nullable final Map<Dimension, JsonNode> dimensions,
            final Pagination<Long> query) {

        return revisions.getOrDefault(key, emptyList()).stream()
                .map(ValueRevision::getRevision)
                // TODO sort order and comparison dependant on pagination direction
                .filter(revision -> query.getPivot() == null || query.getPivot() < revision.getId())
                .sorted(comparingLong(Revision::getId))
                .limit(query.getLimit())
                .collect(toList());
    }

}
