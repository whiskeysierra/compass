package org.zalando.compass.library.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.TaskScheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class DelayedFlywayMigrationStrategyTest {

    private final Clock clock = Clock.fixed(Instant.parse("2017-07-16t11:36:57Z"), ZoneOffset.UTC);
    private final TaskScheduler scheduler = mock(TaskScheduler.class);
    private final DelayedFlywayMigrationStrategy unit = new DelayedFlywayMigrationStrategy(clock, scheduler);
    private final Flyway flyway = mock(Flyway.class);

    @Test
    public void shouldMigrateImmediatelyIfDatabaseIsAvailable() {
        unit.migrate(flyway);

        verifyZeroInteractions(scheduler);
    }

    @Test
    public void shouldDelayMigrationIfDatabaseIsUnavailable() {
        when(flyway.migrate()).thenThrow(FlywayException.class);

        unit.migrate(flyway);

        verify(flyway, times(1)).migrate();

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).schedule(captor.capture(), any(Date.class));

        doReturn(0).when(flyway).migrate();
        final Runnable runnable = captor.getValue();
        runnable.run();

        verify(flyway, times(2)).migrate();
        verifyNoMoreInteractions(scheduler);
    }

}
