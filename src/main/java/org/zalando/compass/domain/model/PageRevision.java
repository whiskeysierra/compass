package org.zalando.compass.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.library.pagination.PageResult;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public class PageRevision<T> implements PageResult<T> {

    Revision revision;

    @Delegate
    PageResult<T> result;

    // TODO overload constructor

}
