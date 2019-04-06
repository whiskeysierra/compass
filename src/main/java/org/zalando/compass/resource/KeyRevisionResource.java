package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.resource.model.KeyCollectionRevisionRepresentation;
import org.zalando.compass.resource.model.KeyRepresentation;
import org.zalando.compass.resource.model.KeyRevisionRepresentation;
import org.zalando.compass.resource.model.RevisionCollectionRepresentation;
import org.zalando.compass.resource.model.RevisionRepresentation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyRevisionResource {

    private final KeyService service;

    @RequestMapping(method = GET, path = "/keys/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<Long, Void> original) {

        final Cursor<Long, Void> cursor = original.with(null, limit);

        final PageResult<Revision> page = service.readPageRevisions(cursor.paginate());

        return paginate(page, cursor,
                c -> link(methodOn(KeyRevisionResource.class).getRevisions(null, c)),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(rev.getId(), null, null)));
    }

    @RequestMapping(method = GET, path = "/keys/revisions/{revision}")
    public ResponseEntity<KeyCollectionRevisionRepresentation> getRevision(
            @PathVariable final long revision,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<String, Void> original) {

        final Cursor<String, Void> cursor = original.with(null, limit);

        final PageRevision<Key> page = service.readPageAt(revision, cursor.paginate());
        final RevisionRepresentation representation = RevisionRepresentation.valueOf(page.getRevision());

        return ResponseEntity.ok(page.render(
                (next, prev, elements) ->
                        new KeyCollectionRevisionRepresentation(representation, next, prev, elements),
                cursor,
                Key::getId,
                c -> link(methodOn(KeyRevisionResource.class).getRevision(revision, null, c)),
                KeyRepresentation::valueOf));
    }

    @RequestMapping(method = GET, path = "/keys/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<Long, Void> original) {

        final Cursor<Long, Void> cursor = original.with(null, limit);
        final PageResult<Revision> page = service.readRevisions(id, cursor.paginate());

        return paginate(page, cursor,
                c -> link(methodOn(KeyRevisionResource.class).getRevisions(id, null, c)),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(id, rev.getId())));
    }

    @RequestMapping(method = GET, path = "/keys/{id}/revisions/{revision}")
    public ResponseEntity<KeyRevisionRepresentation> getRevision(
            @PathVariable final String id,
            @PathVariable final long revision) {
        
        final KeyRevision key = service.readAt(id, revision);
        return ResponseEntity.ok(KeyRevisionRepresentation.valueOf(key));
    }

}
