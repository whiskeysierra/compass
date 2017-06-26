package org.zalando.compass.library;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.ValidatorFactory;
import java.time.Clock;

@Configuration
class ValidatorConfiguration {

    private final Clock clock;

    @Autowired
    public ValidatorConfiguration(final Clock clock) {
        this.clock = clock;
    }

    @Bean
    public ValidatorFactory validatorFactory() {
        return new LocalValidatorFactoryBean() {

            @Override
            protected void postProcessConfiguration(final javax.validation.Configuration<?> configuration) {
                HibernateValidatorConfiguration.class.cast(configuration).timeProvider(clock::millis);
            }

        };
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(final ValidatorFactory factory) {
        final MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidatorFactory(factory);
        return processor;
    }

}
