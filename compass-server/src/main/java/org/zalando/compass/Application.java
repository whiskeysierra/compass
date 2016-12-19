package org.zalando.compass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;

import static org.springframework.context.annotation.FilterType.REGEX;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
@EnableScheduling
@EnableTransactionManagement
public class Application {

    public static final String NAME = "compass";

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
