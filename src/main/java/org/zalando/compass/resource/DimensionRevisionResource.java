package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
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
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.resource.model.DimensionCollectionRevisionRepresentation;
import org.zalando.compass.resource.model.DimensionRepresentation;
import org.zalando.compass.resource.model.DimensionRevisionRepresentation;
import org.zalando.compass.resource.model.RevisionCollectionRepresentation;
import org.zalando.compass.resource.model.RevisionRepresentation;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@RequestMapping(path = "/dimensions")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DimensionRevisionResource {

    private final DimensionService service;

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<Long> cursor) {

        final Pagination<Long> query = Pagination.create(cursor, requireNonNull(limit));
        final PageResult<Revision> page = service.readPageRevisions(query);

        return paginate(page, cursor,
                c -> link(methodOn(DimensionRevisionResource.class).getRevisions(limit, c)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(rev.getId(), null, null)));
    }

    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<DimensionCollectionRevisionRepresentation> getRevision(@PathVariable final long revision,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<String> cursor) {

        final Pagination<String> query = Pagination.create(cursor, requireNonNull(limit));
        final PageRevision<Dimension> page = service.readPageAt(revision, query);

        return ResponseEntity.ok(page.render((next, prev, elements) ->
                new DimensionCollectionRevisionRepresentation(
                        RevisionRepresentation.valueOf(page.getRevision()), next, prev, elements),
                cursor,
                Dimension::getId,
                c -> link(methodOn(DimensionRevisionResource.class).getRevision(revision, limit, c)),
                DimensionRepresentation::valueOf));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @Nullable @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(required = false, defaultValue = "") final Cursor<Long> cursor) {

        final Pagination<Long> query = Pagination.create(cursor, requireNonNull(limit));
        final PageResult<Revision> page = service.readRevisions(id, query);

        return paginate(page, cursor,
                c -> link(methodOn(DimensionRevisionResource.class).getRevisions(id, limit, c)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(id, rev.getId())));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions/{revision}")
    public ResponseEntity<DimensionRevisionRepresentation> getRevision(@PathVariable final String id,
            @PathVariable final long revision) {
        final DimensionRevision dimension = service.readAt(id, revision);
        return ResponseEntity.ok(DimensionRevisionRepresentation.valueOf(dimension));
    }

}
