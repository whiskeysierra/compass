package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;
import org.zalando.compass.library.Pages;

import javax.annotation.Nullable;

@Component
class ReadDimension {

    private final DimensionRepository repository;

    @Autowired
    ReadDimension(final DimensionRepository repository) {
        this.repository = repository;
    }

    Page<Dimension> readPage(@Nullable final String term, final int limit, @Nullable final String after) {
        return Pages.page(repository.findAll(term, limit + 1, after), limit);
    }

    Dimension read(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

}
