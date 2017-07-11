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
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageQuery;
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
@RequestMapping(path = "/dimensions")
class DimensionRevisionResource {

    private final DimensionService service;

    @Autowired
    public DimensionRevisionResource(final DimensionService service) {
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(required = false) final Long after) {

        return paginate(
                () -> service.readPageRevisions(PageQuery.create(after, null, limit)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(limit, rev.getId())),
                // TODO fix
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(limit, null)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(rev.getId())));
    }

    // TODO search by term
    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<DimensionCollectionRevisionRepresentation> getRevision(@PathVariable final long revision) {
        // TODO feed with query parameters
        final PageQuery<String> query = PageQuery.create(null, null, 25);
        final PageRevision<Dimension> page = service.readPageAt(revision, query);
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
                page.hasNext() ? link(methodOn(DimensionRevisionResource.class).getRevision(revision)) : null,
                page.getElements().stream().map(DimensionRepresentation::valueOf).collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final int limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) {

        final PageQuery<Long> query = PageQuery.create(after, before, limit);

        return paginate(
                () -> service.readRevisions(id, query),
                // TODO should omit if limit is default value
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(id, limit, rev.getId(), null)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(id, limit, null, rev.getId())),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(id, rev.getId())));
    }

    // TODO library?!
    private ResponseEntity<RevisionCollectionRepresentation> paginate(final Supplier<PageResult<Revision>> reader,
            final Function<Revision, URI> nexter, final Function<Revision, URI> prever,
            final Function<Revision, URI> linker) {
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
                // direction would be null if we wouldn't paginate already
                page.hasNext() ? nexter.apply(page.getTail()) : null,
                page.hasPrevious() ? prever.apply(page.getHead()) : null,
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
                        link(methodOn(DimensionRevisionResource.class).getRevision(id, rev.getId())),
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
