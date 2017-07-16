package org.zalando.compass.library.flyway;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
class DelayedFlywayMigrationStrategy implements FlywayMigrationStrategy {

    private final Clock clock;
    private final TaskScheduler scheduler;

    @Autowired
    DelayedFlywayMigrationStrategy(final Clock clock, final TaskScheduler scheduler) {
        this.clock = clock;
        this.scheduler = scheduler;
    }

    @Override
    public void migrate(final Flyway flyway) {
        try {
            flyway.migrate();
        } catch (final FlywayException e) {
            log.warn("Delaying flyway migration for 5 seconds, due to:", e);
            final Date inFive = Date.from(Instant.now(clock).plusSeconds(5));
            scheduler.schedule(() -> migrate(flyway), inFive);
        }
    }

}
