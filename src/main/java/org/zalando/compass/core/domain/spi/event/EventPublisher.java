package org.zalando.compass.core.domain.spi.event;

import org.zalando.compass.core.domain.model.event.Event;

public interface EventPublisher {
    void publish(Event event);
}
