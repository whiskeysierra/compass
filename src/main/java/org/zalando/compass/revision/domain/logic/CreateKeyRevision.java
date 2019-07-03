package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.revision.domain.spi.repository.KeyRevisionRepository;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
final class CreateKeyRevision {

    private final KeyRevisionRepository repository;

    void create(final KeyRevision revision) {
        repository.create(revision);
    }

}
