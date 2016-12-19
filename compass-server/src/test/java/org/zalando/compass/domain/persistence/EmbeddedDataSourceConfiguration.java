package org.zalando.compass.domain.persistence;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class EmbeddedDataSourceConfiguration {

    @Bean
    public DataSource dataSource(final EmbeddedPostgres postgres) {
        return postgres.getPostgresDatabase();
    }

    @Bean(destroyMethod = "close")
    public EmbeddedPostgres embeddedPostgres() throws IOException {
        return EmbeddedPostgres.start();
    }

}
