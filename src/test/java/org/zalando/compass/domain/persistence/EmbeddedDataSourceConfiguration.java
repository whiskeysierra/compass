package org.zalando.compass.domain.persistence;

import com.google.common.io.Resources;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;

@Configuration
public class EmbeddedDataSourceConfiguration {

    @Bean
    public DataSource dataSource(final EmbeddedPostgres postgres) throws SQLException, IOException {
        final DataSource dataSource = postgres.getPostgresDatabase();
        final String sql = Resources.toString(getResource("db/init.sql"), UTF_8);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }

        return postgres.getPostgresDatabase(singletonMap("currentSchema", "compass"));
    }

    @Bean(destroyMethod = "close")
    public EmbeddedPostgres embeddedPostgres() throws IOException {
        // 5432 is used by postgres 9.1 on travis
        return EmbeddedPostgres.builder().setPort(5433).start();
    }

}
