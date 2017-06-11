package org.zalando.compass.domain.persistence;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;

import static java.util.Collections.singletonMap;

@Configuration
public class EmbeddedDataSourceConfiguration {

    @Bean
    public DataSource dataSource(final EmbeddedPostgres postgres) throws SQLException {
        final DataSource dataSource = postgres.getPostgresDatabase();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE SCHEMA compass")) {
            statement.executeUpdate();
        }

        return postgres.getPostgresDatabase(singletonMap("currentSchema", "compass"));
    }

    @Bean(destroyMethod = "close")
    public EmbeddedPostgres embeddedPostgres() throws IOException {
        return EmbeddedPostgres.start();
    }

}
