package org.zalando.compass.infrastructure.http;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.api.DimensionRevisionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.revision.DimensionRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.infrastructure.http.model.DimensionCollectionRevisionRepresentation;
import org.zalando.compass.infrastructure.http.model.DimensionRepresentation;
import org.zalando.compass.infrastructure.http.model.DimensionRevisionRepresentation;
import org.zalando.compass.infrastructure.http.model.RevisionCollectionRepresentation;
import org.zalando.compass.infrastructure.http.model.RevisionRepresentation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.infrastructure.http.Linking.link;
import static org.zalando.compass.infrastructure.http.RevisionPaging.paginate;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DimensionRevisionResource {

    private final DimensionRevisionService service;

    @RequestMapping(method = GET, path = "/dimensions/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<Long, Void> original) {

        final Cursor<Long, Void> cursor = original.with(null, limit);
        final PageResult<Revision> page = service.readPageRevisions(cursor.paginate());

        return paginate(page, cursor,
                c -> link(methodOn(DimensionRevisionResource.class).getRevisions(null, c)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(rev.getId(), null, null)));
    }

    @RequestMapping(method = GET, path = "/dimensions/revisions/{revision}")
    public ResponseEntity<DimensionCollectionRevisionRepresentation> getRevision(
            @PathVariable final long revision,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<String, Void> original) {

        final Cursor<String, Void> cursor = original.with(null, limit);
        final PageRevision<Dimension> page = service.readPageAt(revision, cursor.paginate());

        return ResponseEntity.ok(page.render((next, prev, elements) ->
                new DimensionCollectionRevisionRepresentation(
                        RevisionRepresentation.valueOf(page.getRevision()), next, prev, elements),
                cursor,
                Dimension::getId,
                c -> link(methodOn(DimensionRevisionResource.class).getRevision(revision, null, c)),
                DimensionRepresentation::valueOf));
    }

    @RequestMapping(method = GET, path = "/dimensions/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<Long, Void> original) {

        final Cursor<Long, Void> cursor = original.with(null, limit);
        final PageResult<Revision> page = service.readRevisions(id, cursor.paginate());

        return paginate(page, cursor,
                c -> link(methodOn(DimensionRevisionResource.class).getRevisions(id, null, c)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(id, rev.getId())));
    }

    @RequestMapping(method = GET, path = "/dimensions/{id}/revisions/{revision}")
    public ResponseEntity<DimensionRevisionRepresentation> getRevision(
            @PathVariable final String id,
            @PathVariable final long revision) {

        final DimensionRevision dimension = service.readAt(id, revision);
        return ResponseEntity.ok(DimensionRevisionRepresentation.valueOf(dimension));
    }

}
