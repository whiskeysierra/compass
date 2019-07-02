package org.zalando.compass.core.infrastructure.http;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.core.infrastructure.database.model.enums.RevisionType;
import org.zalando.compass.kernel.domain.model.Revision;

import javax.annotation.Nullable;
import java.net.URI;
import java.time.OffsetDateTime;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class RevisionRepresentation {

    Long id;
    OffsetDateTime timestamp;
    URI href;

    @JsonSerialize(converter = LowerCaseConverter.class)
    RevisionType type;
    String user;
    String comment;

    public static RevisionRepresentation valueOf(final Revision revision) {
        return valueOf(revision, null);
    }

    public static RevisionRepresentation valueOf(final Revision revision, @Nullable final URI href) {
        return new RevisionRepresentation(
                revision.getId(),
                revision.getTimestamp(),
                href,
                revision.getType(),
                revision.getUser(),
                revision.getComment()
        );
    }

}
