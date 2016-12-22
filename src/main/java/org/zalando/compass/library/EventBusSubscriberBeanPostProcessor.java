package org.zalando.compass.library;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class EventBusSubscriberBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EventBusSubscriberBeanPostProcessor.class);

    private EventBus bus;

    @Autowired
    public EventBusSubscriberBeanPostProcessor(final EventBus bus) {
        this.bus = bus;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        for (final Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                LOG.debug("Registering [{}] in event bus", bean);
                bus.register(bean);
                return bean;
            }
        }

        return bean;
    }

}