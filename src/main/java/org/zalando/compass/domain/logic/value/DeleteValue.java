package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.logic.Locking;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.domain.persistence.ValueRepository;
import org.zalando.compass.domain.persistence.ValueRevisionRepository;

import java.util.Map;

import static org.zalando.compass.domain.model.Revision.Type.DELETE;

@Slf4j
@Component
class DeleteValue {

    private final Locking locking;
    private final ValueRepository repository;
    private final RevisionService revisionService;
    private final ValueRevisionRepository revisionRepository;

    @Autowired
    DeleteValue(final Locking locking, final ValueRepository repository,
            final RevisionService revisionService, final ValueRevisionRepository revisionRepository) {
        this.locking = locking;
        this.repository = repository;
        this.revisionService = revisionService;
        this.revisionRepository = revisionRepository;
    }

    void delete(final String key, final Map<String, JsonNode> filter) {
        final ValueLock lock = locking.lockValue(key, filter);

        final Value value = lock.getValue();

        if (value == null) {
            throw new NotFoundException();
        }

        repository.delete(key, filter);
        log.info("Deleted value [{}, {}]", key, filter);

        // TODO reuse logic from replace command?
        final Revision revision = revisionService.create(DELETE, "..");
        final ValueRevision valueRevision = value.toRevision(revision);
        revisionRepository.create(key, valueRevision);
        log.info("Created value revision [{}]", valueRevision);
    }

}
