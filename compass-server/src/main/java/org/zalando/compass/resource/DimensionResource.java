package org.zalando.compass.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Dimensions;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/dimensions")
public class DimensionResource {

    @RequestMapping(method = GET)
    public Dimensions getDimensions(@RequestParam(defaultValue = "50") @Min(0) @Max(100) final int limit) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = PUT)
    public Dimensions reorderDimensions(@RequestBody final Dimensions dimensions) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = GET, path = "/{id}")
    public Dimension getDimensions(@RequestParam final String id) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<Dimension> createOrUpdateDimension(@RequestParam final String id,
            @RequestBody final Dimension dimension) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteDimension(@RequestParam final String id) {
        throw new UnsupportedOperationException();
    }

}
