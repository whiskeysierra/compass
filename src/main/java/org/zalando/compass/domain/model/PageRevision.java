package org.zalando.compass.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public class PageRevision<T> {

    @Wither
    Revision revision;

    @Wither
    List<T> elements;

    public <R> PageRevision<R> map(final Function<T, R> function) {
        return new PageRevision<>(revision, elements.stream().map(function).collect(toList()));
    }

    public PageRevision<T> withRevisionTypeUpdate() {
        return withRevision(revision.withType(Revision.Type.UPDATE));
    }

}
