package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.reverse;
import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;
import static org.jooq.SortOrder.ASC;
import static org.jooq.SortOrder.DESC;
import static org.jooq.impl.DSL.val;
import static org.zalando.compass.library.pagination.Direction.BACKWARD;
import static org.zalando.compass.library.pagination.Direction.FORWARD;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class DefaultPagination<P> implements Pagination<P> {

    P pivot;
    int limit;
    Direction direction;

    @Override
    public Pagination<P> increment() {
        return new DefaultPagination<>(pivot, limit + 1, direction);
    }

    @Override
    public SelectForUpdateStep<Record> seek(final SelectOrderByStep<Record> step, final Field<P> field,
            final SortOrder original) {

        return step
                .orderBy(field.sort(apply(original, getDirection())))
                .seekAfter(pivotOf(field.getType()))
                .limit(getLimit());
    }

    private Field<P> pivotOf(final Class<P> type) {
        return getPivot() == null ? null : val(getPivot(), type);
    }

    public SortOrder apply(final SortOrder original, @Nullable final Direction direction) {
        return direction == BACKWARD ? invert(original) : original;
    }

    private SortOrder invert(final SortOrder order) {
        return order == ASC ? DESC : ASC;
    }

    @Override
    public <T> PageResult<T> paginate(final List<T> elements) {
        @Nullable final Direction direction = getDirection();

        final boolean isForward = direction == FORWARD;
        final boolean isBackward = direction == BACKWARD;

        if (isBackward) {
            return getPageResult(reverse(elements), isForward, isBackward);
        }

        return getPageResult(elements, isForward, isBackward);
    }

    private <T> PageResult<T> getPageResult(final List<T> elements, final boolean isForward, final boolean isBackward) {
        final int size = elements.size();

        if (size > getLimit()) {
            if (isBackward) {
                final List<T> items = elements.subList(1, size);
                return createIfNotEmpty(items, true, true);
            } else {
                final List<T> items = elements.subList(0, getLimit());
                return createIfNotEmpty(items, true, isForward);
            }
        } else {
            return createIfNotEmpty(elements, isBackward, isForward);
        }
    }

    public <T> PageResult<T> createIfNotEmpty(final List<T> elements, final boolean next, final boolean previous) {
        return elements.isEmpty() ?
                PageResult.create(emptyList(), false, false) :
                PageResult.create(elements, next, previous);
    }

}
