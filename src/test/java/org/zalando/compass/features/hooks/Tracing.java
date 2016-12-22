package org.zalando.compass.features.hooks;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.tracer.Tracer;

@Component
public class Tracing {

    private final Tracer tracer;

    @Autowired
    public Tracing(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Before
    public void begin() {
        tracer.start();
    }

    @After
    public void rollback() {
        tracer.stop();
}
}
