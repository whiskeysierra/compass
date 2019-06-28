package org.zalando.compass.library;

import org.junit.Test;
import org.zalando.compass.infrastructure.ClockConfiguration;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClockConfigurationTest {

    @Test
    public void shouldUseSystemClock() {
        final Clock clock = new ClockConfiguration().clock();
        assertThat(clock.getZone(), is(ZoneOffset.UTC));
    }

}
