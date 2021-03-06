package org.zalando.compass.core.domain.spi.repository;

import org.zalando.compass.core.domain.model.Revision;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface RevisionRepository {

    long create(OffsetDateTime timestamp, String user, @Nullable String comment);
    Optional<Revision> read(long id);

}
