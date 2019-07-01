package org.zalando.compass.infrastructure.database;

import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.spi.repository.RevisionRepository;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryRevisionRepository implements RevisionRepository {

    private final AtomicLong sequence = new AtomicLong();
    private final ConcurrentMap<Long, Revision> revisions = new ConcurrentHashMap<>();

    @Override
    public long create(final OffsetDateTime timestamp, final String user, @Nullable final String comment) {
        final long id = sequence.incrementAndGet();
        revisions.put(id, new Revision(id, timestamp, null, user, comment));
        return id;
    }

    @Override
    public Optional<Revision> read(final long id) {
        return Optional.ofNullable(revisions.get(id));
    }

}
