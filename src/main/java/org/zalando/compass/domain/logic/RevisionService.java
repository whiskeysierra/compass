package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.RevisionRepository;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.LocalDateTime;

@Repository
public class RevisionService {

    private final Clock clock;
    private final RevisionRepository repository;

    @Autowired
    public RevisionService(final Clock clock, final RevisionRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    public Revision create(final Revision.Type type, final String comment) {
        final LocalDateTime timestamp = LocalDateTime.now(clock);

        // TODO introduce proper dependency
        @Nullable final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String user = authentication == null ? "anonymous" : authentication.getName();

        final Revision revision = new Revision(null, timestamp, type, user, comment);
        final long id = repository.create(revision);

        return revision.withId(id);
    }

}
