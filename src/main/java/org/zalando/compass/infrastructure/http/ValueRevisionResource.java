package org.zalando.compass.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.api.ValueRevisionService;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.revision.ValueRevision;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.infrastructure.http.model.RevisionCollectionRepresentation;
import org.zalando.compass.infrastructure.http.model.RevisionRepresentation;
import org.zalando.compass.infrastructure.http.model.ValueCollectionRevisionRepresentation;
import org.zalando.compass.infrastructure.http.model.ValueRepresentation;
import org.zalando.compass.infrastructure.http.model.ValueRevisionRepresentation;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.infrastructure.http.Linking.link;
import static org.zalando.compass.infrastructure.http.RevisionPaging.paginate;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ValueRevisionResource {

    private final Querying querying;
    private final ValueRevisionService service;

    @RequestMapping(method = GET, path = "/keys/{key}/values/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValuesRevisions(
            @PathVariable final String key,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<Long, Void> original) {

        final Cursor<Long, Void> cursor = original.with(null, limit);
        final PageResult<Revision> page = service.readPageRevisions(key, cursor.paginate());

        return paginate(page, cursor,
                c -> link(methodOn(ValueRevisionResource.class).getValuesRevisions(key, null, c)),
                rev -> link(methodOn(ValueRevisionResource.class).getValuesRevision(key, rev.getId(), of())));
    }

    @RequestMapping(method = GET, path = "/keys/{key}/values/revisions/{revision}")
    public ResponseEntity<ValueCollectionRevisionRepresentation> getValuesRevision(
            @PathVariable final String key,
            @PathVariable final long revision, 
            @RequestParam final Map<String, String> query) {
        
        final Map<String, JsonNode> filter = querying.read(query);
        final PageRevision<Value> page = service.readPageAt(key, filter, revision);

        return ResponseEntity.ok(new ValueCollectionRevisionRepresentation(
                RevisionRepresentation.valueOf(page.getRevision()),
                page.getElements().stream()
                        .map(ValueRepresentation::valueOf)
                        .collect(toList())));
    }

    @RequestMapping(method = GET, path = "/keys/{key}/value/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValueRevisions(
            @PathVariable final String key,
            @RequestParam final Map<String, String> queryParams,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<Long, Map<String, JsonNode>> original) {

        final Map<String, JsonNode> filter = querying.read(queryParams);

        final Cursor<Long, Map<String, JsonNode>> cursor = original.with(filter, limit);
        // TODO remove filter parameter!
        final PageResult<Revision> page = service.readRevisions(key, cursor.getQuery(), cursor.paginate());
        final Map<String, String> normalized = querying.write(cursor.getQuery());

        return paginate(page, cursor,
                c -> link(methodOn(ValueRevisionResource.class).getValueRevisions(key, emptyMap(), null, c)),
                rev -> link(methodOn(ValueRevisionResource.class).getRevision(key, rev.getId(), normalized)));
    }

    @RequestMapping(method = GET, path = "/keys/{key}/value/revisions/{revision}")
    public ResponseEntity<ValueRevisionRepresentation> getRevision(
            @PathVariable final String key,
            @PathVariable final long revision,
            @RequestParam final Map<String, String> query) {
        
        final Map<String, JsonNode> filter = querying.read(query);
        final ValueRevision value = service.readAt(key, filter, revision);
        return ResponseEntity.ok(ValueRevisionRepresentation.valueOf(value));
    }

}
