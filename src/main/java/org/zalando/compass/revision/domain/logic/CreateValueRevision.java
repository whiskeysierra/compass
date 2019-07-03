package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.revision.domain.spi.repository.ValueRevisionRepository;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class CreateValueRevision {

    private final ValueRevisionRepository repository;

    void create(final Key key, final ValueRevision revision) {
        repository.create(key, revision);
    }

}
