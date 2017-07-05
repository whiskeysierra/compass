package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.logic.RevisionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
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
    private final RevisionService revisionService;

    @Autowired
    public DimensionRevisionResource(final DimensionService service,
            final RevisionService revisionService) {
        this.service = service;
        this.revisionService = revisionService;
    }

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<VersionHistory> getRevisions(
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @Nullable @RequestParam(required = false) final Long after) {

        // can actually never happen, just to satisfy IDEA
        checkNotNull(limit, "Limit required");

        return paginate(
                () -> service.readRevisions(limit, after),
                rev -> linkTo(methodOn(DimensionRevisionResource.class).getRevisions(limit, rev.getId())).toUri(),
                rev -> linkTo(methodOn(DimensionRevisionResource.class).getRevision(rev.getId())).toUri());
    }

    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public DimensionPageRevision getRevision(@PathVariable final long revision) {

        final Revision rev = revisionService.read(revision);
        final List<Dimension> dimensions = service.readRevision(revision);

        return new DimensionPageRevision(
                linkTo(methodOn(DimensionResource.class).getAll(null)).toUri(),
                null,
                null,
                new RevisionRepresentation(
                        rev.getId(),
                        rev.getTimestamp(),
                        null,
                        rev.getType(),
                        rev.getUser(),
                        rev.getComment()
                ),
                dimensions
        );
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<VersionHistory> getRevisions(@PathVariable final String id,
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
    private ResponseEntity<VersionHistory> paginate(final Supplier<Page<Revision>> reader,
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

        return ResponseEntity.ok(new VersionHistory(
                null,
                Optional.ofNullable(page.getNext()).map(nexter).orElse(null),
                revisions));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions/{revision}")
    public ResponseEntity<DimensionRevision> getRevision(@PathVariable final String id,
            @PathVariable final long revision) {
        return ResponseEntity.ok(service.readRevision(id, revision));
    }

}
