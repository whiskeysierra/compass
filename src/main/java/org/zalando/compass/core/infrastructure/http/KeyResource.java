package org.zalando.compass.core.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.core.domain.api.KeyService;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.fauxpas.ThrowingUnaryOperator;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.core.infrastructure.http.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.core.infrastructure.http.MediaTypes.JSON_PATCH_VALUE;
import static org.zalando.compass.library.Linking.link;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyResource {

    private final ObjectMapper mapper;
    private final KeyService service;

    @RequestMapping(method = PUT, path = "/keys/{id}")
    public ResponseEntity<KeyRepresentation> createOrReplace(
            @PathVariable final String id,
            @Nullable @RequestHeader(name = IF_NONE_MATCH, required = false) final String ifNoneMatch,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final KeyRepresentation body) {

        final var key = new Key(id, body.getSchema(), body.getDescription());

        final var created = createOrReplace(key, comment, ifNoneMatch);
        final var representation = KeyRepresentation.valueOf(key);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(representation);
    }

    private boolean createOrReplace(final Key key, @Nullable final String comment,
            @Nullable final String ifNoneMatch) {

        if ("*".equals(ifNoneMatch)) {
            service.create(key, comment);
            return true;
        } else {
            return service.replace(key, comment);
        }
    }

    @RequestMapping(method = GET, path = "/keys")
    public ResponseEntity<KeyCollectionRepresentation> getAll(
            @RequestParam(name = "q", required = false) @Nullable final String q,
            @RequestParam(required = false, defaultValue = "25") final Integer limit,
            @RequestParam(name = "cursor", required = false, defaultValue = "") final Cursor<String, String> original) {

        final var cursor = original.with(q, limit);
        final var page = service.readPage(cursor.getQuery(), cursor.paginate());

        return ResponseEntity.ok(page.render(KeyCollectionRepresentation::new,
                cursor, Key::getId,
                c -> link(methodOn(KeyResource.class).getAll(null, null, c)),
                KeyRepresentation::valueOf));
    }

    @RequestMapping(method = GET, path = "/keys/{id}")
    public ResponseEntity<KeyRepresentation> get(@PathVariable final String id) {
        final var revisioned = service.read(id);
        return Conditional.build(revisioned, KeyRepresentation::valueOf);
    }

    @RequestMapping(method = PATCH, path = "/keys/{id}", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<KeyRepresentation> update(
            @PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonMergePatch patch) throws IOException, JsonPatchException {

        return patch(id, comment, patch::apply);
    }

    @RequestMapping(method = PATCH, path = "/keys/{id}", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<KeyRepresentation> update(
            @PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment,
            @RequestBody final JsonPatch patch) throws IOException, JsonPatchException {

        return patch(id, comment, patch::apply);
    }

    private ResponseEntity<KeyRepresentation> patch(final String id, final @Nullable String comment,
            final ThrowingUnaryOperator<JsonNode, JsonPatchException> patch) throws IOException, JsonPatchException {
        final var before = KeyRepresentation.valueOf(service.readOnly(id));
        final var node = mapper.valueToTree(before);

        final var patched = patch.tryApply(node);
        final var after = mapper.treeToValue(patched, KeyRepresentation.class);

        // TODO shouldn't we delegate to a private method here?
        return createOrReplace(id, null, comment, after);
    }

    @RequestMapping(method = DELETE, path = "/keys/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id,
            @Nullable @RequestHeader(name = "Comment", required = false) final String comment) {
        final var key = service.readOnly(id);
        service.delete(key, comment);
    }

}
