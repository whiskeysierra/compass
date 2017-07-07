package org.zalando.compass.domain.model;

import lombok.experimental.Wither;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@lombok.Value
public class PageRevision<T> {

    @Wither
    Revision revision;

    @Wither
    List<T> elements;

    T next;

    public <R> PageRevision<R> map(final Function<T, R> function) {
        return new PageRevision<>(
                revision,
                elements.stream().map(function).collect(toList()),
                next == null ? null : function.apply(next)
        );
    }

    public PageRevision<T> withRevisionTypeUpdate() {
        return withRevision(revision.withType(Revision.Type.UPDATE));
    }

}
