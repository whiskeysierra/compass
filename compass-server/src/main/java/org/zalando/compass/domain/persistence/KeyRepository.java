package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zalando.compass.domain.model.Key;

@Repository
public class KeyRepository {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public KeyRepository(final NamedParameterJdbcTemplate template, final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @SneakyThrows
    public void create(final Key dimension) {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "description", dimension.getDescription());

        template.update("" +
                "INSERT INTO key (id, schema, description)" +
                "     VALUES (:id, :schema::JSONB, :description)", params);
    }

}
