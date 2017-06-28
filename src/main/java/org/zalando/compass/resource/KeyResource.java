package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;
import static org.zalando.compass.resource.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.resource.MediaTypes.JSON_PATCH_VALUE;

@RestController
@RequestMapping(path = "/keys")
class KeyResource {

    private final JsonReader reader;
    private final ObjectMapper mapper;
    private final KeyService service;

    @Autowired
    public KeyResource(final JsonReader reader, final ObjectMapper mapper, final KeyService service) {
        this.reader = reader;
        this.mapper = mapper;
        this.service = service;
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Key> replace(@PathVariable final String id,
            @RequestBody final JsonNode node) throws IOException {

        ensureConsistentId(id, node);
        final Key key = reader.read(node, Key.class);

        final boolean created = service.replace(key);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .body(key);
    }

    private void ensureConsistentId(@PathVariable final String inUrl, final JsonNode node) {
        final JsonNode inBody = node.path("id");

        if (inBody.isMissingNode()) {
            ObjectNode.class.cast(node).put("id", inUrl);
        } else {
            checkArgument(inUrl.equals(inBody.asText()), "If present, ID in body must match with URL");
        }
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Key get(@PathVariable final String id) {
        return service.read(id);
    }

    @RequestMapping(method = GET)
    public KeyPage getAll(@RequestParam(name = "q", required = false) @Nullable final String q) {
        return new KeyPage(service.readAll(q));
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<Key> update(@PathVariable final String id,
            @RequestBody final ObjectNode patch) throws IOException, JsonPatchException {

        final Key key = service.read(id);
        final ObjectNode node = mapper.valueToTree(key);

        final JsonMergePatch mergePatch = JsonMergePatch.fromJson(patch);
        final JsonNode patched = mergePatch.apply(node);
        return replace(id, patched);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<Key> update(@PathVariable final String id,
            @RequestBody final ArrayNode patch) throws IOException, JsonPatchException {

        // TODO validate JsonPatch schema?

        final Key key = service.read(id);
        final JsonNode node = mapper.valueToTree(key);

        final JsonPatch jsonPatch = JsonPatch.fromJson(patch);
        final JsonNode patched = jsonPatch.apply(node);
        return replace(id, patched);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final String id) {
        service.delete(id);
    }

}
