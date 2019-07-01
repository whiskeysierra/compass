package org.zalando.compass.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.api.DimensionRevisionService;
import org.zalando.compass.domain.api.NotFoundException;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.domain.spi.repository.DimensionRepository;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadDimension {

    private final DimensionRepository repository;
    private final DimensionRevisionService revisionService;

    PageResult<Dimension> readPage(@Nullable final String term, final Pagination<String> query) {
        final List<Dimension> dimensions = repository.findAll(term, query.increment());
        return query.paginate(dimensions);
    }

    Dimension readOnly(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

    Revisioned<Dimension> read(final String id) {
        final Dimension dimension = readOnly(id);
        final Revision revision = readLatestRevision(id);
        return Revisioned.create(dimension, revision);
    }

    @Nullable //since it should be eventually consistent
    private Revision readLatestRevision(final String id) {
        final Pagination<Long> pagination = Cursor.<Long, Void>initial().with(null, 1).paginate();
        final PageResult<Revision> revisions = revisionService.readRevisions(id, pagination);
        return revisions.getElements().isEmpty() ? null : revisions.getHead();
    }

}
