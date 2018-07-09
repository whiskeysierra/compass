package org.zalando.compass.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.zalando.compass.domain.model.Revisioned;

import java.time.OffsetDateTime;
import java.util.Optional;
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

        Optional.ofNullable(revisioned.getRevision()).map(Conditional::eTag).ifPresent(builder::eTag);
        Optional.ofNullable(revisioned.getTimestamp()).map(Conditional::lastModified).ifPresent(builder::lastModified);

        return builder;
    }

    private static <T> String eTag(final Long revision) {
        return new ETag(revision).toString();
    }

    private static <T> long lastModified(final OffsetDateTime timestamp) {
        return timestamp.toInstant().toEpochMilli();
    }


}
