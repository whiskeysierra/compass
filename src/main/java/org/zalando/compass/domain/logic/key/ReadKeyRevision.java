package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.persistence.KeyRevisionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadKeyRevision {

    private final KeyRevisionRepository revisionRepository;

    @Autowired
    ReadKeyRevision(final KeyRevisionRepository revisionRepository) {
        this.revisionRepository = revisionRepository;
    }

    Page<Revision> readAll(final int limit, @Nullable final Long after) {
        return revisionRepository.findAll(limit, after);
    }

    List<Key> read(final long revision) {
        return revisionRepository.find(revision);
    }

    Page<Revision> readAll(final String id, final int limit, @Nullable final Long after) {
        return revisionRepository.findAll(id, limit, after);
    }

    KeyRevision read(final String id, final long revision) {
        return revisionRepository.find(id, revision).orElseThrow(NotFoundException::new);
    }

}
