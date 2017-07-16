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
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

import java.io.IOException;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.compass.resource.Linking.link;
import static org.zalando.compass.resource.RevisionPaging.paginate;

@RestController
@RequestMapping(path = "/dimensions")
class DimensionRevisionResource {

    private final JsonReader reader;
    private final DimensionService service;

    @Autowired
    public DimensionRevisionResource(JsonReader reader, final DimensionService service) {
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(
            @RequestParam(required = false, defaultValue = "25") final String limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) throws IOException {

        final Pagination<Long> query = Pagination.create(after, before,
                reader.read("Limit", limit, int.class));
        final PageResult<Revision> page = service.readPageRevisions(query);

        return paginate(page,
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(limit, rev.getId(), null)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(limit, null, rev.getId())),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(rev.getId(), null, null, null)));
    }

    @RequestMapping(method = GET, path = "/revisions/{revision}")
    public ResponseEntity<DimensionCollectionRevisionRepresentation> getRevision(@PathVariable final long revision,
            @Nullable@RequestParam(required = false, defaultValue = "25") final String limit,
            @Nullable @RequestParam(value = "_after", required = false) final String after,
            @Nullable @RequestParam(value = "_before", required = false) final String before) throws IOException {

        final Pagination<String> query = Pagination.create(after, before,
                reader.read("Limit", limit, int.class));
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
                page.hasNext() ? link(methodOn(DimensionRevisionResource.class)
                        .getRevision(revision, limit, page.getTail().getId(), null)) : null,
                page.hasPrevious() ? link(methodOn(DimensionRevisionResource.class)
                        .getRevision(revision, limit, null, page.getHead().getId())) : null,
                page.getElements().stream().map(DimensionRepresentation::valueOf).collect(toList())
        ));
    }

    @RequestMapping(method = GET, path = "/{id}/revisions")
    public ResponseEntity<RevisionCollectionRepresentation> getRevisions(@PathVariable final String id,
            @RequestParam(required = false, defaultValue = "25") final String limit,
            @Nullable @RequestParam(value = "_after", required = false) final Long after,
            @Nullable @RequestParam(value = "_before", required = false) final Long before) throws IOException {

        final Pagination<Long> query = Pagination.create(after, before,
                reader.read("Limit", limit, int.class));
        final PageResult<Revision> page = service.readRevisions(id, query);

        return paginate(page,
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(id, limit, rev.getId(), null)),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevisions(id, limit, null, rev.getId())),
                rev -> link(methodOn(DimensionRevisionResource.class).getRevision(id, rev.getId())));
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
                        null,
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
