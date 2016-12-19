package org.zalando.compass.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Keys;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/keys")
public class KeyResource {

    @RequestMapping(method = GET)
    public Keys getKeys(@RequestParam(defaultValue = "50") @Min(0) @Max(100) final int limit) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Key getKeys(@RequestParam final String id) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Key> createOrUpdateKey(@RequestParam final String id,
            @RequestBody final Key key) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteKey(@RequestParam final String id) {
        throw new UnsupportedOperationException();
    }
    
    
    
}
