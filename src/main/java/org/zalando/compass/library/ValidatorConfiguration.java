package org.zalando.compass.library;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
                configuration.clockProvider(() -> clock);
            }

        };
    }

}
