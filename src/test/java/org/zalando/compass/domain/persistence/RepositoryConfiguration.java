package org.zalando.compass.domain.persistence;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.zalando.compass.library.jooq.DaoPostProcessor;

@TestConfiguration
@ImportAutoConfiguration({
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        FlywayAutoConfiguration.class,
        TransactionAutoConfiguration.class,
        JooqAutoConfiguration.class,
})
@Import({
        DaoPostProcessor.class,
        EmbeddedDataSourceConfiguration.class,
})
public class RepositoryConfiguration {
}
