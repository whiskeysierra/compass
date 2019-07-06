package org.zalando.compass.revision.domain.logic;

import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.spi.repository.RevisionRepository;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

final class InMemoryRevisionRepository implements RevisionRepository {

    private final AtomicLong sequence = new AtomicLong();
    private final ConcurrentMap<Long, Revision> revisions = new ConcurrentHashMap<>();

    @Override
    public long create(final OffsetDateTime timestamp, final String user, @Nullable final String comment) {
        final var id = sequence.incrementAndGet();
        revisions.put(id, new Revision(id, timestamp, null, user, comment));
        return id;
    }

    @Override
    public Optional<Revision> read(final long id) {
        return Optional.ofNullable(revisions.get(id));
    }

}
