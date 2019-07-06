package org.zalando.compass.core.domain.logic;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.revision.domain.api.DimensionRevisionService;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadDimension {

    private final DimensionRepository repository;
    private final DimensionRevisionService revisionService;

    PageResult<Dimension> readPage(@Nullable final String term, final Pagination<String> query) {
        final var dimensions = repository.findAll(term, query.increment());
        return query.paginate(ImmutableList.copyOf(dimensions));
    }

    Dimension readOnly(final String id) {
        return repository.find(id).orElseThrow(NotFoundException::new);
    }

    Revisioned<Dimension> read(final String id) {
        final var dimension = readOnly(id);
        final var revision = readLatestRevision(id);
        return Revisioned.create(dimension, revision);
    }

    @Nullable //since it should be eventually consistent
    private Revision readLatestRevision(final String id) {
        final var pagination = Cursor.<Long, Void>initial().with(null, 1).paginate();
        final var revisions = revisionService.readRevisions(id, pagination);
        return revisions.getElements().isEmpty() ? null : revisions.getHead();
    }

}
