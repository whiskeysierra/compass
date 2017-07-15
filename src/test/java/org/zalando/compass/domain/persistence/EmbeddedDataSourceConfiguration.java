package org.zalando.compass.domain.persistence;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class EmbeddedDataSourceConfiguration {

    // TODO remove @Bean(destroyMethod = "close")
    public EmbeddedPostgres embeddedPostgres() throws IOException {
        // 5432 is used by postgres 9.1 on travis
        return EmbeddedPostgres.builder().setPort(5433).start();
    }

}
