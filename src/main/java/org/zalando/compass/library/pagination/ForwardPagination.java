package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import java.util.List;

import static org.jooq.impl.DSL.val;

@Getter
@AllArgsConstructor
final class ForwardPagination<P> implements Pagination<P> {

    private final P pivot;
    private final int limit;

    @Override
    public Pagination<P> increment() {
        return new ForwardPagination<>(pivot, limit + 1);
    }

    @Override
    public SelectForUpdateStep<Record> seek(final SelectOrderByStep<Record> step, final Field<P> field,
            final SortOrder order) {

        return step
                .orderBy(field.sort(order))
                .seekAfter(val(pivot, field.getType()))
                .limit(limit);
    }

    @Override
    public <T> PageResult<T> paginate(final List<T> elements) {
        return getPageResult(elements);
    }

    private <T> PageResult<T> getPageResult(final List<T> elements) {
        final var size = elements.size();

        if (size > limit) {
            return createIfNotEmpty(elements.subList(0, limit), true);
        } else {
            return createIfNotEmpty(elements, false);
        }
    }

    private <T> PageResult<T> createIfNotEmpty(final List<T> elements, final boolean next) {
        return elements.isEmpty() ?
                PageResult.create(elements, false, false) :
                PageResult.create(elements, next, true);
    }

}
