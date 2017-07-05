package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadDimensionRevision {

    private final DimensionRevisionRepository repository;

    @Autowired
    ReadDimensionRevision(final DimensionRevisionRepository repository) {
        this.repository = repository;
    }

    Page<Revision> readAll(final int limit, @Nullable final Long after) {
        return repository.findAll(limit, after);
    }

    List<Dimension> read(final long revision) {
        return repository.find(revision);
    }

    Page<Revision> readAll(final String id, final int limit, @Nullable final Long after) {
        return repository.findAll(id, limit, after);
    }

    DimensionRevision read(final String id, final long revision) {
        return repository.find(id, revision).orElseThrow(NotFoundException::new);
    }

}
