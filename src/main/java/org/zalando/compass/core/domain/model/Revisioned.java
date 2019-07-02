package org.zalando.compass.core.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public final class Revisioned<T> {

    Revision revision;
    T entity;

    public static <T> Revisioned<T> create(final T entity, @Nullable final Revision revision) {
        return new Revisioned<>(revision, entity);
    }

}

