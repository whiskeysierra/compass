package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.RevisionRepository;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
class ReadKeyRevision {

    private final KeyRevisionRepository repository;
    private final RevisionRepository revisionRepository;

    @Autowired
    ReadKeyRevision(final KeyRevisionRepository repository, final RevisionRepository revisionRepository) {
        this.repository = repository;
        this.revisionRepository = revisionRepository;
    }

    PageResult<Revision> readPageRevisions(final int limit, @Nullable final Long after) {
        final List<Revision> revisions = repository.findPageRevisions(limit + 1, after).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());

        final PageQuery<Long> query = PageQuery.create(after, null, limit);
        return query.paginate(revisions);
    }

    PageRevision<Key> readPageAt(final long revisionId, final int limit, @Nullable final String after) {
        final Revision revision = revisionRepository.read(revisionId)
                .orElseThrow(NotFoundException::new)
                .withTypeUpdate();

        final PageQuery<String> query = PageQuery.create(after, null, limit);
        final List<Key> keys = repository.findPage(revisionId, limit, after);

        return new PageRevision<>(revision, query.paginate(keys));
    }

    PageResult<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        final List<Revision> revisions = repository.findRevisions(id, limit + 1, after);
        final PageQuery<Long> query = PageQuery.create(after, null, limit);
        return query.paginate(revisions);
    }

    KeyRevision readAt(final String id, final long revision) {
        return repository.find(id, revision)
                .orElseThrow(NotFoundException::new);
    }

}
