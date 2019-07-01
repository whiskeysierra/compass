package org.zalando.compass.infrastructure.http;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Revisioned;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.function.Function;

final class Conditional {

    private Conditional() {

    }

    static <F, T> ResponseEntity<T> build(final Revisioned<F> revisioned, final Function<F, T> mapper) {
        return builder(revisioned)
                .body(mapper.apply(revisioned.getEntity()));
    }

    static <T> BodyBuilder builder(final Revisioned<T> revisioned) {
        final BodyBuilder builder = ResponseEntity.ok();

        @Nullable final Revision revision = revisioned.getRevision();

        if (revision == null) {
            return builder;
        }

        return builder
                .eTag(eTag(revision.getId()))
                .lastModified(lastModified(revision.getTimestamp()));
    }

    private static String eTag(final Long revision) {
        return new ETag(revision).toString();
    }

    private static long lastModified(final OffsetDateTime timestamp) {
        return timestamp.toInstant().toEpochMilli();
    }


}
