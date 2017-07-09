package org.zalando.compass.domain.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.RevisionRepository;

import javax.annotation.Nullable;
import java.security.Principal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Repository
public class RevisionService {

    private final Clock clock;
    private final RevisionRepository repository;

    @Autowired
    public RevisionService(final Clock clock, final RevisionRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    public Revision create(@Nullable final String comment) {
        final OffsetDateTime timestamp = OffsetDateTime.now(clock);

        final String user = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName).orElse("anonymous");

        final Revision input = new Revision(null, timestamp, null, user, comment);
        final long id = repository.create(input);

        final Revision revision = input.withId(id);
        log.info("Created revision [{}].", revision);
        return revision;
    }

}
