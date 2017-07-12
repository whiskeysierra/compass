package org.zalando.compass.library.pagination;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import javax.annotation.Nullable;
import java.util.List;

public interface Pagination<P> {

    @Nullable
    P getPivot();

    int getLimit();

    Pagination<P> increment();

    @Nullable
    Direction getDirection();

    enum Direction {
        FORWARD, BACKWARD
    }

    static <C> Pagination<C> create(@Nullable final C after, @Nullable final C before, final int limit) {
        if (after == null && before == null) {
            return new DefaultPagination<>(null, limit, null);
        } else if (after == null) {
            return new DefaultPagination<>(before, limit, Direction.BACKWARD);
        } else if (before == null) {
            return new DefaultPagination<>(after, limit, Direction.FORWARD);
        } else {
            throw new IllegalPageQueryException("after and before are mutually exclusive");
        }
    }

    SelectForUpdateStep<Record> seek(SelectOrderByStep<Record> step, Field<P> field, SortOrder order);

    <T> PageResult<T> paginate(List<T> elements);

}
