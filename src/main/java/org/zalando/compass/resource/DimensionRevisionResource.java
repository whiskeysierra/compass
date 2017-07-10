package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/dimensions")
class DimensionRevisionResource {

    private final DimensionService service;

    @Autowired
    public DimensionRevisionResource(final DimensionService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @Nullable @RequestParam(required = false) final Long after) {

        // can actually never happen, just to satisfy IDEA
        checkNotNull(limit, "Limit required");

        return paginate(
                () -> service.readPageRevisions(limit, after),
                rev -> linkTo(methodOn(DimensionRevisionResource.class).getRevisions(limit, rev.getId())).toUri(),
                rev -> linkTo(methodOn(DimensionRevisionResource.class).getRevision(rev.getId())).toUri());
    }

    // TODO search by term
    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<DimensionCollectionRevisionRepresentation> getRevision(@PathVariable final long revision) {

        final PageRevision<Dimension> page = service.readPageAt(revision);
        final Revision rev = page.getRevision();

        return ResponseEntity.ok(new DimensionCollectionRevisionRepresentation(
                new RevisionRepresentation(
                        rev.getId(),
                        rev.getTimestamp(),
                        null,
                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                page.getElements().stream().map(DimensionRepresentation::valueOf).collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @Nullable @RequestParam(required = false) final Long after) {

        // can actually never happen, just to satisfy IDEA
        checkNotNull(limit, "Limit required");

        return paginate(
                () -> service.readRevisions(id, limit, after),
                // TODO should omit if limit is default value
                rev -> linkTo(methodOn(DimensionRevisionResource.class).getRevisions(id, limit, rev.getId())).toUri(),
                rev -> linkTo(methodOn(DimensionRevisionResource.class).getRevision(id, rev.getId())).toUri());
    }

    // TODO library?!
    private ResponseEntity<RevisionCollectionRepresentation> paginate(final Supplier<Page<Revision>> reader,
            final Function<Revision, URI> nexter, final Function<Revision, URI> linker) {
        final Page<Revision> page = reader.get();

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
                null,
                Optional.ofNullable(page.getNext()).map(nexter).orElse(null),
                revisions));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions/{revision}")
    public ResponseEntity<DimensionRevisionRepresentation> getRevision(@PathVariable final String id,
            @PathVariable final long revision) {
        final DimensionRevision dimension = service.readAt(id, revision);
        final Revision rev = dimension.getRevision();
        return ResponseEntity.ok(new DimensionRevisionRepresentation(
                dimension.getId(),
                new RevisionRepresentation(
                        rev.getId(),
                        rev.getTimestamp(),
                        null, // TOD generate links..

                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                dimension.getSchema(),
                dimension.getRelation(),
                dimension.getDescription()
        ));
    }

}
