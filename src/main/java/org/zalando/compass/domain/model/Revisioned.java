package org.zalando.compass.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public final class Revisioned<T> {

    Long revision;
    OffsetDateTime timestamp;
    T entity;

    public static <T> Revisioned<T> create(final T entity, @Nullable final Revision revision) {
        return revision == null ?
                new Revisioned<>(null, null, entity) :
                new Revisioned<>(revision.getId(), revision.getTimestamp(), entity);
    }

}

