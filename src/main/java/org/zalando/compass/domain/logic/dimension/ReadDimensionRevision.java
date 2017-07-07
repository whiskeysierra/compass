package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;

import static java.util.stream.Collectors.toList;

@Component
class ReadDimensionRevision {

    private final DimensionRevisionRepository repository;

    @Autowired
    ReadDimensionRevision(final DimensionRevisionRepository repository) {
        this.repository = repository;
    }

    Page<Revision> readPageRevisions(final int limit, @Nullable final Long after) {
        final Page<Revision> page = repository.findPageRevisions(limit, after);

        return page.withElements(page.getElements().stream()
                .map(Revision::withTypeUpdate)
                .collect(toList()));
    }

    PageRevision<Dimension> readPageAt(final long revision) {
        return repository.findPage(revision)
                .orElseThrow(NotFoundException::new)
                .withRevisionTypeUpdate();
    }

    Page<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return repository.findRevisions(id, limit, after);
    }

    DimensionRevision readAt(final String id, final long revision) {
        return repository.find(id, revision)
                .orElseThrow(NotFoundException::new);
    }

}
