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
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.library.pagination.Cursor.create;
import static org.zalando.compass.library.pagination.Direction.BACKWARD;
import static org.zalando.compass.library.pagination.Direction.FORWARD;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@RequestMapping(path = "/keys")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyRevisionResource {

    private final KeyService service;

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<Long> cursor) {

        final Pagination<Long> query = Pagination.create(cursor, requireNonNull(limit));
        final PageResult<Revision> page = service.readPageRevisions(query);

        return paginate(page,
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(limit, create(FORWARD, rev.getId()))),
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(limit, create(BACKWARD, rev.getId()))),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(rev.getId(), null, null)));
    }

    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<KeyCollectionRevisionRepresentation> getRevision(@PathVariable final long revision,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<String> cursor) {

        final Pagination<String> query = Pagination.create(cursor, requireNonNull(limit));
        final PageRevision<Key> page = service.readPageAt(revision, query);
        final Revision rev = page.getRevision();

        return ResponseEntity.ok(new KeyCollectionRevisionRepresentation(
                new RevisionRepresentation(
                        rev.getId(),
                        rev.getTimestamp(),
                        null,
                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                page.hasNext() ? link(methodOn(KeyRevisionResource.class)
                        .getRevision(revision, limit, create(FORWARD, page.getTail().getId()))) : null,
                page.hasPrevious() ? link(methodOn(KeyRevisionResource.class)
                        .getRevision(revision, limit, create(BACKWARD, page.getHead().getId()))) : null,
                page.getElements().stream().map(KeyRepresentation::valueOf).collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<Long> cursor) {

        final Pagination<Long> query = Pagination.create(cursor, requireNonNull(limit));
        final PageResult<Revision> page = service.readRevisions(id, query);

        return paginate(page,
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(id, limit, create(FORWARD, rev.getId()))),
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(id, limit, create(BACKWARD, rev.getId()))),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(id, rev.getId())));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions/{revision}")
    public ResponseEntity<KeyRevisionRepresentation> getRevision(@PathVariable final String id,
            @PathVariable final long revision) {
        final KeyRevision key = service.readAt(id, revision);
        final Revision rev = key.getRevision();
        return ResponseEntity.ok(new KeyRevisionRepresentation(
                key.getId(),
                new RevisionRepresentation(
                        rev.getId(),
                        rev.getTimestamp(),
                        null,
                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                key.getSchema(),
                key.getDescription()
        ));
    }

}
