package org.zalando.compass.library.pagination;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import java.util.List;

public interface Pagination<P> {

    Pagination<P> increment();

    static <C> Pagination<C> create(final Cursor<C> cursor, int limit) {
        return new DefaultPagination<>(cursor.getPivot(), limit, cursor.getDirection());
    }

    // TODO find a way that enforces increment, seek + paginate in one step
    SelectForUpdateStep<Record> seek(SelectOrderByStep<Record> step, Field<P> field, SortOrder order);

    <T> PageResult<T> paginate(List<T> elements);

}
