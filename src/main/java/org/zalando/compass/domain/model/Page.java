package org.zalando.compass.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public class Page<T> {

    List<T> elements;
    T next;

    public PageRevision<T> toRevision(final Revision revision) {
        return new PageRevision<>(revision, elements, next);
    }

}
