package org.zalando.compass.domain.logic.revision;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.NotFoundException;
import org.zalando.compass.domain.logic.ValueSelector;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.revision.ValueRevision;
import org.zalando.compass.domain.repository.RevisionRepository;
import org.zalando.compass.domain.repository.revision.ValueRevisionRepository;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ReadValueRevision {

    private final ValueRevisionRepository repository;
    private final RevisionRepository revisionRepository;
    private final ValueSelector selector;

    public PageResult<Revision> readPageRevisions(final String key, final Pagination<Long> query) {
        final List<Revision> revisions = repository.findPageRevisions(key, query.increment()).stream()
                .map(Revision::withTypeUpdate)
                .collect(toList());

        return query.paginate(revisions);
    }

    public PageRevision<Value> readPageAt(final String key, final Map<String, JsonNode> filter, final long revisionId) {
        final Revision revision = revisionRepository.read(revisionId)
                .orElseThrow(NotFoundException::new)
                .withTypeUpdate();

        final List<Value> values = repository.findPage(key, revisionId)
                .stream().map(ValueRevision::toValue).collect(toList());

        if (filter.isEmpty()) {
            // special case, just for reading many values
            return new PageRevision<>(revision, PageResult.create(values, false, false));
        }

        return new PageRevision<>(revision, PageResult.create(selector.select(values, filter), false, false));
    }

    public PageResult<Revision> readRevisions(final String key, final Map<String, JsonNode> dimensions,
            final Pagination<Long> query) {
        final List<Revision> revisions = repository.findRevisions(key, dimensions, query.increment());
        return query.paginate(revisions);
    }

    public ValueRevision readAt(final String key, final Map<String, JsonNode> dimensions, final long revision) {
        final List<ValueRevision> values = repository.findValueRevisions(key, revision);
        final List<ValueRevision> matched = selector.select(values, dimensions);

        return matched.stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

}
