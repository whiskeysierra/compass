package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.RevisionRepository;

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

    Page<Revision> readPageRevisions(final int limit, @Nullable final Long after) {
        final Page<Revision> page = repository.findPageRevisions(limit, after);

        return page.withElements(page.getElements().stream()
                .map(Revision::withTypeUpdate)
                .collect(toList()));
    }

    PageRevision<Key> readPageAt(final long revisionId) {
        final Revision revision = revisionRepository.read(revisionId)
                .orElseThrow(NotFoundException::new)
                .withTypeUpdate();

        final List<Key> keys = repository.findPage(revisionId);

        return new PageRevision<>(revision, keys);
    }

    Page<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return repository.findRevisions(id, limit, after);
    }

    KeyRevision readAt(final String id, final long revision) {
        return repository.find(id, revision)
                .orElseThrow(NotFoundException::new);
    }

}
