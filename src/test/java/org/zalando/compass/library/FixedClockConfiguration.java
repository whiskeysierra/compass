package org.zalando.compass.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Configuration
public class FixedClockConfiguration {

    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(Instant.parse("2017-07-07T22:09:21Z"), ZoneOffset.UTC);
    }

}
