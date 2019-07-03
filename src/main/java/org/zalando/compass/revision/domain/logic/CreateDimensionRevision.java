package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class CreateDimensionRevision {

    private final DimensionRevisionRepository repository;

    void create(final DimensionRevision revision) {
        repository.create(revision);
    }

}
