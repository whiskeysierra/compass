package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.KeyRepository;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadKey {

    private final KeyRepository repository;
    private final KeyRevisionRepository revisionRepository;

    @Autowired
    ReadKey(final KeyRepository repository,
            final KeyRevisionRepository revisionRepository) {
        this.repository = repository;
        this.revisionRepository = revisionRepository;
    }

    List<Key> readAll(@Nullable final String term) {
        return repository.findAll(term);
    }

    Key read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

    // TODO ReadRevision command?

    Page<Revision> readRevisions(final int limit, @Nullable final Long after) {
        return revisionRepository.findAll(limit, after);
    }

    List<Key> readRevision(final long revision) {
        return revisionRepository.find(revision);
    }

    Page<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return revisionRepository.findAll(id, limit, after);
    }

    KeyRevision readRevision(final String id, final long revision) {
        return revisionRepository.find(id, revision).orElseThrow(NotFoundException::new);
    }

}
