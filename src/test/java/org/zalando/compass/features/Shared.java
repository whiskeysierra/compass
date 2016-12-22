package org.zalando.compass.features;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zuchini.spring.ScenarioScoped;

import static org.junit.Assert.assertTrue;

@Component
@ScenarioScoped
@Data
public class Shared {

    private ResponseEntity<?> response;

    public <T> T getBodyAs(final Class<T> type) {
        assertTrue("Expected 2xx, but was " + response.getStatusCodeValue(),
                response.getStatusCode().is2xxSuccessful());
        return type.cast(response.getBody());
    }

}
