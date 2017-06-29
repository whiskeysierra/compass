package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Component
class ReadDimension {

    private final DimensionRepository repository;

    @Autowired
    ReadDimension(final DimensionRepository repository) {
        this.repository = repository;
    }

    Dimension read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

    List<Dimension> readAll(@Nullable final String term) {
        return repository.findAll(term);
    }

    public List<DimensionRevision> readRevisions(final String id) {
        return repository.findAllRevisions(id);
    }

    public DimensionRevision readRevision(final String id, final long revision) {
        return repository.findRevision(id, revision).orElseThrow(NotFoundException::new);
    }

}
