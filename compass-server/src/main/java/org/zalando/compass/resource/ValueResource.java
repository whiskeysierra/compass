package org.zalando.compass.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/keys/{id}")
public class ValueResource {

    @RequestMapping(method = GET, path = "/value")
    public Value getValue(@RequestParam final String id, @RequestParam final Map<String, String> filter) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = GET, path = "/values")
    public Values getValues(@RequestParam final String id, @RequestParam final Map<String, String> filter) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = PUT, path = "/values")
    public Values replaceValues(@RequestParam final String id, @RequestBody final Values values) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = POST, path = "/values")
    public Values addValues(@RequestParam final String id, @RequestBody final Values values) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = DELETE, path = "/values")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteValues(@RequestParam final String id) {
        throw new UnsupportedOperationException();
    }

}
