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
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;

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
            @Nullable @RequestParam(required = false) final Long after) {

        return paginate(
                () -> service.readPageRevisions(limit, after),
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(limit, rev.getId())),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(rev.getId())));
    }

    // TODO search by term
    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<KeyCollectionRevisionRepresentation> getRevision(@PathVariable final long revision) {

        final PageRevision<Key> page = service.readPageAt(revision, 25, null);
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
                page.hasNext() ? link(methodOn(KeyRevisionResource.class).getRevision(revision)) : null,
                page.getElements().stream().map(KeyRepresentation::valueOf).collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(required = false) final Long after) {

        return paginate(
                () -> service.readRevisions(id, limit, after),
                // TODO should omit if limit is default value
                rev -> link(methodOn(KeyRevisionResource.class).getRevisions(id, limit, rev.getId())),
                rev -> link(methodOn(KeyRevisionResource.class).getRevision(id, rev.getId())));
    }

    // TODO library?!
    private ResponseEntity<RevisionCollectionRepresentation> paginate(final Supplier<PageResult<Revision>> reader,
            final Function<Revision, URI> nexter, final Function<Revision, URI> linker) {
        final PageResult<Revision> page = reader.get();

        final List<RevisionRepresentation> revisions = page.getElements().stream()
                .map(revision -> new RevisionRepresentation(
                        revision.getId(),
                        revision.getTimestamp(),
                        linker.apply(revision),
                        revision.getType(),
                        revision.getUser(),
                        revision.getComment()
                ))
                .collect(toList());

        return ResponseEntity.ok(new RevisionCollectionRepresentation(
                page.hasNext() ? nexter.apply(page.getTail()) : null,
                null,
                revisions));
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
