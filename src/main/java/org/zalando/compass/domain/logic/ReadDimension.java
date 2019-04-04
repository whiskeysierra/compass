package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;

@Component
class ReadDimension {

    private final DimensionRepository repository;

    @Autowired
    ReadDimension(final DimensionRepository repository) {
        this.repository = repository;
    }

    PageResult<Dimension> readPage(@Nullable final String term, final Pagination<String> query) {
        // TODO the increment is needed to make subList work - they should be way closer together
        final List<Dimension> dimensions = repository.findAll(term, query.increment());
        return query.paginate(dimensions);
    }

    Dimension read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

}
