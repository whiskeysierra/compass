package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.spi.repository.RevisionRepository;

import javax.annotation.Nullable;
import java.security.Principal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultRevisionService implements RevisionService {

    private final Clock clock;
    private final RevisionRepository repository;

    // TODO we shouldn't rely on the request context here
    @Override
    public Revision create(@Nullable final String comment) {
        final var timestamp = OffsetDateTime.now(clock);

        final var user = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName).orElse("anonymous");

        final var id = repository.create(timestamp, user, comment);

        final var revision = new Revision(id, timestamp, null, user, comment);
        log.info("Created revision [{}].", revision);
        return revision;
    }

    @Override
    public Revision read(final long revisionId) {
        return repository.read(revisionId)
                .orElseThrow(NotFoundException::new);
    }

}
