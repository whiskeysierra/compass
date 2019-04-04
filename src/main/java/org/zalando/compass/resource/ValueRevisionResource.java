package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.resource.model.RevisionCollectionRepresentation;
import org.zalando.compass.resource.model.RevisionRepresentation;
import org.zalando.compass.resource.model.ValueCollectionRevisionRepresentation;
import org.zalando.compass.resource.model.ValueRepresentation;
import org.zalando.compass.resource.model.ValueRevisionRepresentation;

import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@RequestMapping(path = "/keys/{key}")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ValueRevisionResource {

    private final Querying querying;
    private final ValueService service;

    @RequestMapping(method = GET, path = "/values/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValuesRevisions(@PathVariable final String key,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<Long> cursor) {

        final Pagination<Long> query = Pagination.create(cursor, requireNonNull(limit));
        final PageResult<Revision> page = service.readPageRevisions(key, query);

        return paginate(page, cursor,
                c -> link(methodOn(ValueRevisionResource.class).getValuesRevisions(key, limit, c)),
                rev -> link(methodOn(ValueRevisionResource.class).getValuesRevision(key, rev.getId(), of())));
    }

    @RequestMapping(method = GET, path = "/values/revisions/{revision}")
    public ResponseEntity<ValueCollectionRevisionRepresentation> getValuesRevision(@PathVariable final String key,
            @PathVariable final long revision, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final PageRevision<Value> page = service.readPageAt(key, filter, revision);

        return ResponseEntity.ok(new ValueCollectionRevisionRepresentation(
                RevisionRepresentation.valueOf(page.getRevision()),
                page.getElements().stream()
                        .map(ValueRepresentation::valueOf)
                        .collect(toList())));
    }

    @RequestMapping(method = GET, path = "/value/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValueRevisions(@PathVariable final String key,
            @RequestParam final Map<String, String> queryParams,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<Long> cursor) {

        final Map<String, JsonNode> filter = querying.read(queryParams);
        final Pagination<Long> query = Pagination.create(cursor, requireNonNull(limit));

        final PageResult<Revision> page = service.readRevisions(key, filter, query);
        final Map<String, String> normalized = querying.write(filter);

        return paginate(page, cursor.withQuery(normalized),
                c -> link(methodOn(ValueRevisionResource.class).getValueRevisions(key, emptyMap(), limit, c)),
                rev -> link(methodOn(ValueRevisionResource.class).getRevision(key, rev.getId(), normalized)));
    }

    @RequestMapping(method = GET, path = "/value/revisions/{revision}")
    public ResponseEntity<ValueRevisionRepresentation> getRevision(@PathVariable final String key,
            @PathVariable final long revision,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final ValueRevision value = service.readAt(key, filter, revision);
        return ResponseEntity.ok(ValueRevisionRepresentation.valueOf(value));
    }

}
