package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/keys/{key}")
class ValueRevisionResource {

    private final JsonQueryParser parser;
    private final ValueService service;

    @Autowired
    public ValueRevisionResource(final JsonQueryParser parser, final ValueService service) {
        this.parser = parser;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/values/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getValuesRevisions(@PathVariable final String key) {
        final Page<Revision> page = service.readPageRevisions(key);
        final List<Revision> revisions = page.getElements();

        final List<RevisionRepresentation> representations = revisions.stream()
                .map(revision -> new RevisionRepresentation(
                        revision.getId(),
                        revision.getTimestamp(),
                        linkTo(methodOn(ValueRevisionResource.class).getValuesRevision(key, revision.getId(), emptyMap())).toUri(),
                        revision.getType(),
                        revision.getUser(),
                        revision.getComment()
                ))
                .collect(toList());

        return ResponseEntity.ok(new RevisionCollectionRepresentation(null, representations));
    }

    @RequestMapping(method = GET, path = "/values/revisions/{revision}")
    public ResponseEntity<ValueCollectionRevisionRepresentation> getValuesRevision(@PathVariable final String key,
            @PathVariable final long revision, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
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
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        final Page<Revision> page = service.readRevisions(key, filter);
        final List<Revision> revisions = page.getElements();

        final List<RevisionRepresentation> representations = revisions.stream()
                .map(revision -> new RevisionRepresentation(
                        revision.getId(),
                        revision.getTimestamp(),
                        linkTo(methodOn(ValueRevisionResource.class).getRevision(key, revision.getId(), query)).toUri(),
                        revision.getType(),
                        revision.getUser(),
                        revision.getComment()
                ))
                .collect(toList());

        return ResponseEntity.ok(new RevisionCollectionRepresentation(null, representations));
    }

    @RequestMapping(method = GET, path = "/value/revisions/{revision}")
    public ResponseEntity<ValueRevisionRepresentation> getRevision(@PathVariable final String key, @PathVariable final long revision,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        return ResponseEntity.ok(ValueRevisionRepresentation.valueOf(service.readAt(key, filter, revision)));
    }

}
