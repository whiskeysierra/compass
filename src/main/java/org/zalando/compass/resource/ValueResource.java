package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;
import org.zalando.compass.domain.persistence.NotFoundException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.resource.MediaTypes.JSON_MERGE_PATCH_VALUE;
import static org.zalando.compass.resource.MediaTypes.JSON_PATCH_VALUE;

@RestController
@RequestMapping(path = "/keys/{key}")
class ValueResource {

    private final JsonQueryParser parser;
    private final JsonReader reader;
    private final ObjectMapper mapper;
    private final ValueService service;

    @Autowired
    public ValueResource(final JsonQueryParser parser, final JsonReader reader, final ObjectMapper mapper,
            final ValueService service) {
        this.parser = parser;
        this.reader = reader;
        this.mapper = mapper;
        this.service = service;
    }

    @RequestMapping(method = PUT, path = "/values")
    public ValuePage replaceAll(@PathVariable final String key, @RequestBody final JsonNode node) throws IOException {
        final List<Value> values = reader.read(node, ValuePage.class).getValues().stream()
                .map(value -> value.withDimensions(firstNonNull(value.getDimensions(), ImmutableMap.of())))
                .collect(toList());

        service.replace(key, values);

        return readAll(key, emptyMap());
    }

    @RequestMapping(method = PUT, path = "/value")
    public ResponseEntity<Value> replace(@PathVariable final String key, @RequestParam final Map<String, String> query,
            @RequestBody final JsonNode node) throws IOException {

        final ImmutableMap<String, JsonNode> dimensions = parser.parse(query);
        final Value value = reader.read(node, Value.class).withDimensions(dimensions);

        final boolean created = service.replace(key, value);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .location(canonicalUrl(key, value))
                .body(value);
    }

    @RequestMapping(method = GET, path = "/values")
    public ValuePage readAll(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        return new ValuePage(service.readAll(key, filter));
    }

    @RequestMapping(method = GET, path = "/value")
    public ResponseEntity<Value> read(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        try {
            final Value value = service.read(key, filter);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_LOCATION, canonicalUrl(key, value).toASCIIString())
                    .body(value);
        } catch (final NotFoundException e) {
            // TODO limit 1
            final List<ValueRevision> revisions = service.readRevisions(key, filter);

            if (revisions.isEmpty()) {
                throw e;
            }

            // TODO getOnlyElement
            final long revision = revisions.get(0).getRevision().getId();

            return ResponseEntity
                    .status(GONE)
                    .location(linkTo(methodOn(ValueRevisionResource.class).getRevision(key, revision, render(filter))).toUri())
                    .build();
        }
    }

    @RequestMapping(method = PATCH, path = "/values", consumes = {APPLICATION_JSON_VALUE, JSON_PATCH_VALUE})
    public ValuePage updateAll(@PathVariable final String key,
            @RequestBody final ArrayNode patch) throws IOException, JsonPatchException {

        // TODO validate JsonPatch schema?

        final ValuePage values = readAll(key, emptyMap());
        final JsonNode node = mapper.valueToTree(values);

        final JsonPatch jsonPatch = JsonPatch.fromJson(patch);
        final JsonNode patched = jsonPatch.apply(node);
        return replaceAll(key, patched);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = {APPLICATION_JSON_VALUE, JSON_MERGE_PATCH_VALUE})
    public ResponseEntity<Value> update(@PathVariable final String key, @RequestParam final Map<String, String> query,
            @RequestBody final ObjectNode patch) throws IOException, JsonPatchException {

        final Map<String, JsonNode> filter = parser.parse(query);
        final Value value = service.read(key, filter);
        final JsonNode node = mapper.valueToTree(value);

        final JsonMergePatch jsonPatch = JsonMergePatch.fromJson(patch);
        final JsonNode patched = jsonPatch.apply(node);
        return replace(key, query, patched);
    }

    @RequestMapping(method = PATCH, path = "/{id}", consumes = JSON_PATCH_VALUE)
    public ResponseEntity<Value> update(@PathVariable final String key, @RequestParam final Map<String, String> query,
            @RequestBody final ArrayNode patch) throws IOException, JsonPatchException {

        // TODO validate JsonPatch schema?

        final Map<String, JsonNode> filter = parser.parse(query);
        final Value value = service.read(key, filter);
        final JsonNode node = mapper.valueToTree(value);

        final JsonPatch jsonPatch = JsonPatch.fromJson(patch);
        final JsonNode patched = jsonPatch.apply(node);
        return replace(key, query, patched);
    }

    // TODO shouldn't this be singular?!
    @RequestMapping(method = DELETE, path = "/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        service.delete(key, filter);
    }

    private URI canonicalUrl(final String key, final Value value) {
        final Map<String, String> query = render(value.getDimensions());
        return linkTo(methodOn(ValueResource.class).read(key, query)).toUri();
    }

    // TODO document sort
    private Map<String, String> render(final Map<String, JsonNode> filter) {
        return ImmutableSortedMap.copyOf(
                Maps.transformValues(filter, JsonNode::asText));
    }

}
