package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.ValueRevision;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(path = "/keys/{key}")
class ValueRevisionResource {

    private final JsonQueryParser parser;
    private final ValueService service;

    @Autowired
    public ValueRevisionResource(final JsonQueryParser parser, final ValueService service) {
        this.parser = parser;
        this.service = service;
    }

    @RequestMapping(method = GET, path = "/value/revisions")
    public ValueRevisionPage getRevisions(@PathVariable final String key,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        final List<ValueRevision> revisions = service.readRevisions(key, filter);
        return new ValueRevisionPage(revisions);
    }

    @RequestMapping(method = GET, path = "/value/revisions/{revision}")
    public ResponseEntity<ValueRevision> getRevision(@PathVariable final String key, @PathVariable final long revision,
            @RequestParam final Map<String, String> query) {
        final Map<String, JsonNode> filter = parser.parse(query);
        return ResponseEntity.ok(service.readRevision(key, filter, revision));
    }

    // TODO /values/revisions
    // TODO /values/revisions/{revision}

}
