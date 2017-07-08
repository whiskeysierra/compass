package org.zalando.compass;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.Test;
import org.zalando.compass.library.Changed;
import org.zalando.compass.library.ClockConfiguration;
import org.zalando.compass.library.Pages;
import org.zalando.compass.library.Tables;
import org.zalando.compass.resource.MediaTypes;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Hack
@OhNoYouDidnt
public final class EnforceCoverageTest {

    @Test
    public void shouldUseChangedConstructor() {
        new Changed();
    }

    @Test
    public void shouldUseSystemClock() {
        final Clock clock = new ClockConfiguration().clock();
        assertThat(clock.getZone(), is(ZoneOffset.UTC));
    }

    @Test
    public void shouldUseMediaTypesConstructor() {
        new MediaTypes();
    }

    @Test
    public void shouldUsePagesConstructor() {
        new Pages();
    }

    @Test
    public void shouldUseTablesConstructor() {
        new Tables();
    }

}
