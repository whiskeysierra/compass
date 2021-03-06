package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

// TODO those shouldn't be spring beans!
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadDimensionRevision {

    private final DimensionRevisionRepository repository;
    private final RevisionService service;

    PageResult<Revision> readPageRevisions(final Pagination<Long> query) {
        final var revisions = repository.findPageRevisions(query.increment()).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());

        return query.paginate(revisions);
    }

    PageRevision<Dimension> readPageAt(final long revisionId, final Pagination<String> query) {
        final var revision = service.read(revisionId).withTypeUpdate();

        final var dimensions = repository.findPage(revisionId, query.increment());
        final var page = query.paginate(dimensions);

        return new PageRevision<>(revision, page);
    }

    PageResult<Revision> readRevisions(final String id, final Pagination<Long> query) {
        final var revisions = repository.findRevisions(id, query.increment());
        // TODO shouldn't this be done by the repository?!
        return query.paginate(revisions);
    }

    DimensionRevision readAt(final String id, final long revision) {
        return repository.find(id, revision)
                .orElseThrow(NotFoundException::new);
    }

}
