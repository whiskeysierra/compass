package org.zalando.compass.revision.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.revision.domain.model.ValueRevision;
import org.zalando.compass.revision.domain.model.ValueRevisions;
import org.zalando.compass.revision.domain.spi.repository.ValueRevisionRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadValueRevision {

    private final ValueRevisionRepository repository;
    private final RevisionService service;

    public PageResult<Revision> readPageRevisions(final String key, final Pagination<Long> query) {
        final List<Revision> revisions = repository.findPageRevisions(key, query.increment()).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());

        return query.paginate(revisions);
    }

    public PageRevision<ValueRevision> readPageAt(final String key, final Map<Dimension, JsonNode> filter, final long revisionId) {
        final Revision revision = service.read(revisionId)
                .withTypeUpdate();

        final ValueRevisions values = repository.findPage(key, revisionId);

        if (filter.isEmpty()) {
            // special case, just for reading many values
            return new PageRevision<>(revision, PageResult.create(values.getValues(), false, false));
        }

        return new PageRevision<>(revision, PageResult.create(values.select(filter), false, false));
    }

    public PageResult<Revision> readRevisions(final String key, final Map<Dimension, JsonNode> dimensions,
            final Pagination<Long> query) {
        final List<Revision> revisions = repository.findRevisions(key, dimensions, query.increment());
        return query.paginate(revisions);
    }

    public ValueRevision readAt(final String key, final Map<Dimension, JsonNode> dimensions, final long revision) {
        return repository.findValueRevisions(key, revision).selectOne(dimensions);
    }

}
