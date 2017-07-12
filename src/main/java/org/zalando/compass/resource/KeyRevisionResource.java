package org.zalando.compass.resource;

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
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@RequestMapping(path = "/keys")
class KeyRevisionResource {

    private final KeyService service;

    @Autowired
    public KeyRevisionResource(final KeyService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) {

        final Pagination<Long> query = Pagination.create(after, before, limit);
        final PageResult<Revision> page = service.readPageRevisions(query);

        return paginate(page,
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(limit, rev.getId(), null)),
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(limit, null, rev.getId())),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(rev.getId(), 25, null, null)));
    }

    // TODO search by term
    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<KeyCollectionRevisionRepresentation> getRevision(@PathVariable final long revision,
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(value = "_after", required = false) final String after,
            @Nullable @RequestParam(value = "_before", required = false) final String before) {

        final Pagination<String> query = Pagination.create(after, before, limit);
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
                        .getRevision(revision, limit, page.getTail().getId(), null)) : null,
                page.hasPrevious() ? link(methodOn(KeyRevisionResource.class)
                        .getRevision(revision, limit, null, page.getHead().getId())) : null,
                page.getElements().stream().map(KeyRepresentation::valueOf).collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) {

        final Pagination<Long> query = Pagination.create(after, before, limit);
        final PageResult<Revision> page = service.readRevisions(id, query);

        return paginate(page,
                // TODO should omit if limit is default value
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(id, limit, rev.getId(), null)),
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(id, limit, null, rev.getId())),
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
                        link(methodOn(KeyRevisionResource.class).getRevision(id, rev.getId())),
                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                key.getSchema(),
                key.getDescription()
        ));
    }

}
