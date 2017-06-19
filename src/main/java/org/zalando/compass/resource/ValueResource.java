package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.zalando.compass.resource.Entries.Entry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zalando.compass.domain.logic.BadArgumentException.checkArgument;

@RestController
class ValueResource {

    private final JsonQueryParser parser;
    private final JsonReader reader;
    private final ValueService service;

    @Autowired
    public ValueResource(final JsonQueryParser parser, final JsonReader reader,
            final ValueService service) {
        this.parser = parser;
        this.reader = reader;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/keys/{key}/values")
    public ValuePage readAll(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        return new ValuePage(service.readAllByKey(key, filter));
    }

    @RequestMapping(method = GET, path = "/keys/{key}/value")
    public ResponseEntity<Value> read(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        final Value value = service.read(key, filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, canonicalUrl(key, value).toASCIIString())
                .body(value);
    }

    @RequestMapping(method = PUT, path = "/keys/{key}/values")
    public ValuePage replaceAll(@PathVariable final String key, @RequestBody final JsonNode node) throws IOException {
        final List<Value> values = reader.read(node, ValuePage.class).getValues().stream()
                .map(value -> value.getDimensions() == null ? value.withDimensions(ImmutableMap.of()) : value)
                .collect(toList());

        service.replace(key, values);

        return new ValuePage(values);
    }

    @RequestMapping(method = PUT, path = "/keys/{key}/value")
    public ResponseEntity<Value> replace(@PathVariable final String key, @RequestParam final Map<String, String> query,
            @RequestBody final JsonNode node) throws IOException {

        final ImmutableMap<String, JsonNode> dimensions = parser.parse(query);
        final Value input = reader.read(node, Value.class);
        final Value value = ensureConsistentDimensions(dimensions, input);

        final boolean created = service.replace(key, value);

        return ResponseEntity
                .status(created ? CREATED : OK)
                .location(canonicalUrl(key, value))
                .body(value);
    }

    private Value ensureConsistentDimensions(final ImmutableMap<String, JsonNode> dimensions, final Value input) {
        checkArgument(input.getDimensions() == null || dimensions.equals(input.getDimensions()),
                "If present, dimensions must match with URL");

        return input.withDimensions(dimensions);
    }

    @RequestMapping(method = DELETE, path = "/keys/{key}/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String key, @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        service.delete(key, filter);
    }

    @RequestMapping(method = GET, path = "/values")
    public Entries readAll(@RequestParam(name = "q", required = false) @Nullable final String q) {
        return new Entries(service.readAllByKeyPattern(q).entrySet().stream()
            .collect(toImmutableMap(e -> e.getKey().getId(), e -> new Entry(e.getValue()))));
    }

    private URI canonicalUrl(final String key, final Value value) {
        final Map<String, String> dimensions = ImmutableSortedMap.copyOf(
                Maps.transformValues(value.getDimensions(), JsonNode::asText));
        return linkTo(methodOn(ValueResource.class).read(key, dimensions)).toUri();
    }

}
