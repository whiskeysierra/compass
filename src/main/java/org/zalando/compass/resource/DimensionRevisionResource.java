package org.zalando.compass.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
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
    public VersionHistory getRevisions() {
        // TODO implement
        return new VersionHistory(null, null, emptyList());
    }

    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public DimensionPageRevision getRevision(@PathVariable final long revision) {
        // TODO get latest revision of each dimension up to revision id, exclude DELETE
        return new DimensionPageRevision(
                linkTo(methodOn(DimensionResource.class).getAll(null)).toUri(),
                null,
                null,
                null,
                emptyList()
        );
    }


    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<VersionHistory> getRevisions(@PathVariable final String id,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @Nullable @RequestParam(required = false) final Long after) {

        // can actually never happen, just to satisfy IDEA
        checkNotNull(limit, "Limit required");

        final Page<Revision> page = service.readRevisions(id, limit, after);

        final URI next = page.getNext() == null ?
                null :
                // TODO should omit if limit is default value
                linkTo(methodOn(DimensionRevisionResource.class)
                        .getRevisions(id, limit, page.getNext().getId())).toUri();

        final List<RevisionRepresentation> revisions = page.getElements().stream()
                .map(revision -> new RevisionRepresentation(
                        revision.getId(),
                        revision.getTimestamp(),
                        linkTo(methodOn(DimensionRevisionResource.class).getRevision(id, revision.getId())).toUri(),
                        revision.getType(),
                        revision.getUser(),
                        revision.getComment()
                ))
                .collect(toList());

        return ResponseEntity.ok(new VersionHistory(null, next, revisions));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions/{revision}")
    public ResponseEntity<DimensionRevision> getRevision(@PathVariable final String id,
            @PathVariable final long revision) {
        return ResponseEntity.ok(service.readRevision(id, revision));
    }

}
