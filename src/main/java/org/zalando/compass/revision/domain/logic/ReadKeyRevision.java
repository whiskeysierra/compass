package org.zalando.compass.revision.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.kernel.domain.model.PageRevision;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.revision.domain.spi.repository.KeyRevisionRepository;
import org.zalando.compass.core.domain.spi.repository.RevisionRepository;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadKeyRevision {

    private final KeyRevisionRepository repository;
    private final RevisionRepository revisionRepository;

    PageResult<Revision> readPageRevisions(final Pagination<Long> query) {
        final List<Revision> revisions = repository.findPageRevisions(query.increment()).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());

        return query.paginate(revisions);
    }

    PageRevision<Key> readPageAt(final long revisionId, final Pagination<String> query) {
        final Revision revision = revisionRepository.read(revisionId)
                .orElseThrow(NotFoundException::new)
                .withTypeUpdate();

        final List<Key> keys = repository.findPage(revisionId, query.increment());
        final PageResult<Key> page = query.paginate(keys);

        return new PageRevision<>(revision, page);
    }

    PageResult<Revision> readRevisions(final String id, final Pagination<Long> query) {
        final List<Revision> revisions = repository.findRevisions(id, query.increment());
        return query.paginate(revisions);
    }

    KeyRevision readAt(final String id, final long revision) {
        return repository.find(id, revision)
                .orElseThrow(NotFoundException::new);
    }

}