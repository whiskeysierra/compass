package org.zalando.compass.library.jooq;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class DaoPostProcessor implements BeanPostProcessor {

    private final Configuration configuration;

    @Autowired
    public DaoPostProcessor(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof DAOImpl) {
            DAOImpl.class.cast(bean).setConfiguration(configuration);
        }
        return bean;
    }
}