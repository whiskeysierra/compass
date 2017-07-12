package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.DimensionRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.RevisionRepository;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
class ReadDimensionRevision {

    private final DimensionRevisionRepository repository;
    private final RevisionRepository revisionRepository;

    @Autowired
    ReadDimensionRevision(final DimensionRevisionRepository repository, final RevisionRepository revisionRepository) {
        this.repository = repository;
        this.revisionRepository = revisionRepository;
    }

    PageResult<Revision> readPageRevisions(final PageQuery<Long> query) {
        final List<Revision> revisions = repository.findPageRevisions(query.increment()).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());

        return query.paginate(revisions);
    }

    PageRevision<Dimension> readPageAt(final long revisionId, final PageQuery<String> query) {
        final Revision revision = revisionRepository.read(revisionId)
                .orElseThrow(NotFoundException::new)
                .withTypeUpdate();

        final List<Dimension> dimensions = repository.findPage(revisionId, query.increment());
        final PageResult<Dimension> page = query.paginate(dimensions);

        return new PageRevision<>(revision, page);
    }

    PageResult<Revision> readRevisions(final String id, final PageQuery<Long> query) {
        final List<Revision> revisions = repository.findRevisions(id, query.increment());
        return query.paginate(revisions);
    }

    DimensionRevision readAt(final String id, final long revision) {
        return repository.find(id, revision)
                .orElseThrow(NotFoundException::new);
    }

}
