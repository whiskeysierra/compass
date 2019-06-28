package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.NotFoundException;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.repository.ValueRepository;
import org.zalando.compass.domain.repository.ValueRevisionRepository;

import javax.annotation.Nullable;
import java.util.Map;

import static org.zalando.compass.infrastructure.database.model.enums.RevisionType.DELETE;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DeleteValue {

    private final Locking locking;
    private final ValueRepository repository;
    private final RevisionService revisionService;
    private final ValueRevisionRepository revisionRepository;

    void delete(final String key, final Map<String, JsonNode> filter, @Nullable final String comment) {
        final ValueLock lock = locking.lockValue(key, filter);

        final Value value = lock.getValue();

        if (value == null) {
            throw new NotFoundException();
        }

        final Revision rev = revisionService.create(comment).withType(DELETE);

        delete(key, value, rev);
    }

    void delete(final String key, final Value value, final Revision rev) {
        repository.delete(key, value.getDimensions());
        log.info("Deleted value [{}, {}]", key, value.getDimensions());

        final ValueRevision revision = value.toRevision(rev);
        revisionRepository.create(key, revision);
        log.info("Created value revision [{}]", revision);
    }

}
