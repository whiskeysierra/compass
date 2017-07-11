package org.zalando.compass.library.pagination;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static org.jooq.SortOrder.ASC;
import static org.jooq.SortOrder.DESC;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.library.pagination.PageQuery.Direction.BACKWARD;
import static org.zalando.compass.library.pagination.PageQuery.Direction.FORWARD;

public interface PageQuery<P> {

    @Nullable
    P getPivot();

    int getLimit();

    PageQuery<P> increment();

    @Nullable
    Direction getDirection();

    enum Direction {
        FORWARD, BACKWARD
    }

    static <C> PageQuery<C> create(@Nullable final C after, @Nullable final C before, final int limit) {
        if (after == null && before == null) {
            return new DefaultPageQuery<>(null, limit, null);
        } else if (after == null) {
            return new DefaultPageQuery<>(before, limit, BACKWARD);
        } else if (before == null) {
            return new DefaultPageQuery<>(after, limit, Direction.FORWARD);
        } else {
            throw new IllegalPageQueryException("after and before are mutually exclusive");
        }
    }

    // TODO should probably be somewhere else...
    default SelectForUpdateStep<Record> seek(final SelectOrderByStep<Record> step, final Field<P> field,
            final SortOrder order) {
        final Field<P> pivot = getPivot() == null ? null : val(getPivot(), field.getType());

        if (getDirection() == BACKWARD) {
            final SortOrder inverse = order == ASC ? DESC : ASC;

            return step
                    .orderBy(field.sort(inverse))
                    .seekAfter(pivot)
                    .limit(getLimit());
        } else {
            return step
                    .orderBy(field.sort(order))
                    .seekAfter(pivot)
                    .limit(getLimit());
        }
    }

    default <T> PageResult<T> paginate(final List<T> elements) {
        @Nullable final Direction direction = getDirection();

        if (direction == BACKWARD) {
            Collections.reverse(elements);
        }

        if (elements.size() > getLimit()) {
            if (direction == BACKWARD) {
                final List<T> items = elements.subList(1, elements.size());
                return PageResult.create(items, true, true);
            } else {
                final List<T> items = elements.subList(0, getLimit());
                return PageResult.create(items, true, direction == FORWARD);
            }
        } else {
            return PageResult.create(elements, direction == BACKWARD, direction == FORWARD);
        }
    }

}
