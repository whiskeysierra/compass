package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@RequestMapping(path = "/keys/{key}")
class ValueRevisionResource {

    private final Querying querying;
    private final JsonReader reader;
    private final ValueService service;

    @Autowired
    public ValueRevisionResource(final Querying querying, final JsonReader reader, final ValueService service) {
        this.querying = querying;
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/values/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValuesRevisions(@PathVariable final String key,
            @RequestParam(required = false, defaultValue = "25") final String limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) throws IOException {

        final Pagination<Long> query = Pagination.create(after, before,
                reader.read("Limit", limit, int.class));
        final PageResult<Revision> page = service.readPageRevisions(key, query);

        return paginate(page,
                rev -> link(methodOn(ValueRevisionResource.class).getValuesRevisions(key, limit, rev.getId(), null)),
                rev -> link(methodOn(ValueRevisionResource.class).getValuesRevisions(key, limit, null, rev.getId())),
                rev -> link(methodOn(ValueRevisionResource.class).getValuesRevision(key, rev.getId(), of())));
    }

    @RequestMapping(method = GET, path = "/values/revisions/{revision}")
    public ResponseEntity<ValueCollectionRevisionRepresentation> getValuesRevision(@PathVariable final String key,
            @PathVariable final long revision, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        final PageRevision<Value> page = service.readPageAt(key, filter, revision);
        final Revision rev = page.getRevision();

        return ResponseEntity.ok(new ValueCollectionRevisionRepresentation(
                new RevisionRepresentation(
                        rev.getId(),
                        rev.getTimestamp(),
                        null,
                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                page.getElements().stream()
                    .map(ValueRepresentation::valueOf)
                    .collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/value/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValueRevisions(@PathVariable final String key,
            @RequestParam final Map<String, String> queryParams,
            @RequestParam(required = false, defaultValue = "25") final String limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) throws IOException {

        final Map<String, JsonNode> filter = querying.read(queryParams);
        final Pagination<Long> query = Pagination.create(after, before,
                reader.read("Limit", limit, int.class));

        final PageResult<Revision> page = service.readRevisions(key, filter, query);
        final Map<String, String> normalized = querying.write(filter);

        return paginate(page,
                rev -> link(methodOn(ValueRevisionResource.class).getValueRevisions(key, normalized, limit, rev.getId(), null)),
                rev -> link(methodOn(ValueRevisionResource.class).getValueRevisions(key, normalized, limit, null, rev.getId())),
                rev -> link(methodOn(ValueRevisionResource.class).getRevision(key, rev.getId(), normalized)));
    }

    @RequestMapping(method = GET, path = "/value/revisions/{revision}")
    public ResponseEntity<ValueRevisionRepresentation> getRevision(@PathVariable final String key, @PathVariable final long revision,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = querying.read(query);
        return ResponseEntity.ok(ValueRevisionRepresentation.valueOf(service.readAt(key, filter, revision)));
    }

}
