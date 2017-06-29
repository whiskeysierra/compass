package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadDimension {

    private final DimensionRepository repository;
    private final DimensionRevisionRepository revisionRepository;

    @Autowired
    ReadDimension(final DimensionRepository repository,
            final DimensionRevisionRepository revisionRepository) {
        this.repository = repository;
        this.revisionRepository = revisionRepository;
    }

    Dimension read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

    List<Dimension> readAll(@Nullable final String term) {
        return repository.findAll(term);
    }

    public Page<DimensionRevision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return revisionRepository.findAll(id, limit, after);
    }

    public DimensionRevision readRevision(final String id, final long revision) {
        return revisionRepository.find(id, revision).orElseThrow(NotFoundException::new);
    }

}
