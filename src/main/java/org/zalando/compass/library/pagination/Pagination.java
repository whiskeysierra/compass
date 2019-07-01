package org.zalando.compass.library.pagination;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import java.util.List;

public interface Pagination<P> {

    P getPivot();

    int getLimit();

    Pagination<P> increment();

    // TODO move this part of pagination to a separate interface/class to decouple it from jOOQ
    // TODO find a way that enforces increment, seek + paginate in one step
    SelectForUpdateStep<Record> seek(SelectOrderByStep<Record> step, Field<P> field, SortOrder order);

    <T> PageResult<T> paginate(List<T> elements);

}
