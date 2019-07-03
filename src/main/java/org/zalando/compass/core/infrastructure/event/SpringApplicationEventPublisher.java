package org.zalando.compass.core.infrastructure.event;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.zalando.compass.core.domain.model.event.Event;
import org.zalando.compass.core.domain.spi.event.EventPublisher;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class SpringApplicationEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(final Event event) {
        publisher.publishEvent(event);
    }

}
