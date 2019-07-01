package org.zalando.compass.library.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SortOrder;

import java.util.List;

@Getter
@AllArgsConstructor
final class InitialPagination<P> implements Pagination<P> {

    private final int limit;

    @Override
    public P getPivot() {
        return null;
    }

    @Override
    public Pagination<P> increment() {
        return new InitialPagination<>(limit + 1);
    }

    @Override
    public SelectForUpdateStep<Record> seek(final SelectOrderByStep<Record> step, final Field<P> field,
            final SortOrder order) {

        return step
                .orderBy(field.sort(order))
                .limit(limit);
    }

    @Override
    public <T> PageResult<T> paginate(final List<T> elements) {
        return getPageResult(elements);
    }

    private <T> PageResult<T> getPageResult(final List<T> elements) {
        final int size = elements.size();

        if (size > limit) {
            return createIfNotEmpty(elements.subList(0, limit), true);
        } else {
            return createIfNotEmpty(elements, false);
        }
    }

    private <T> PageResult<T> createIfNotEmpty(final List<T> elements, final boolean next) {
        return elements.isEmpty() ?
                PageResult.create(elements, false, false) :
                PageResult.create(elements, next, false);
    }

}
