package org.zalando.compass.library.pagination;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.reverse;
import static org.jooq.SortOrder.ASC;
import static org.jooq.SortOrder.DESC;
import static org.jooq.impl.DSL.val;

@AllArgsConstructor
final class BackwardPagination<P> implements Pagination<P> {

    private static final Map<SortOrder, SortOrder> SORT_ORDERS = ImmutableMap.of(
            ASC, DESC,
            DESC, ASC
    );

    private final P pivot;
    private final int limit;

    @Override
    public Pagination<P> increment() {
        return new BackwardPagination<>(pivot, limit + 1);
    }

    @Override
    public SelectForUpdateStep<Record> seek(final SelectOrderByStep<Record> step, final Field<P> field,
            final SortOrder order) {

        return step
                .orderBy(field.sort(invert(order)))
                .seekAfter(pivotOf(field.getType()))
                .limit(limit);
    }

    private Field<P> pivotOf(final Class<P> type) {
        return pivot == null ? null : val(pivot, type);
    }

    private SortOrder invert(final SortOrder order) {
        return SORT_ORDERS.get(order);
    }

    @Override
    public <T> PageResult<T> paginate(final List<T> elements) {
        return getPageResult(reverse(elements));
    }

    private <T> PageResult<T> getPageResult(final List<T> elements) {
        final int size = elements.size();

        if (size > limit) {
            return createIfNotEmpty(elements.subList(1, size), true);
        } else {
            return createIfNotEmpty(elements, false);
        }
    }

    private <T> PageResult<T> createIfNotEmpty(final List<T> elements, final boolean previous) {
        return elements.isEmpty() ?
                PageResult.create(elements, false, false) :
                PageResult.create(elements, true, previous);
    }

}
