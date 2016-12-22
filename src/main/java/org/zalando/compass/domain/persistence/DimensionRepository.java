package org.zalando.compass.domain.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gag.annotation.remark.Hack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Dimension;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.springframework.dao.support.DataAccessUtils.singleResult;
import static org.zalando.compass.domain.persistence.DimensionCriteria.dimensions;
import static org.zalando.fauxpas.FauxPas.throwingBiFunction;

@Component
public class DimensionRepository implements Repository<Dimension, String, DimensionCriteria> {

    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper mapper;

    @Autowired
    public DimensionRepository(final NamedParameterJdbcTemplate template, final ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @Override
    public boolean create(final Dimension dimension) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "relation", dimension.getRelation(),
                "description", dimension.getDescription());

        try {
            template.update("" +
                    "INSERT INTO dimension (id, schema, relation, description)" +
                    "VALUES (:id, :schema::JSONB, :relation, :description)", params);
            return true;
        } catch (final DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public Optional<Dimension> find(final String id) throws IOException {
        final List<Dimension> dimensions = findAll(dimensions(singleton(id)));
        @Nullable final Dimension dimension = singleResult(dimensions);
        return Optional.ofNullable(dimension);
    }

    @Override
    public List<Dimension> findAll(final DimensionCriteria criteria) throws IOException {
        final Set<String> dimensions = criteria.getDimensions();

        if (dimensions.isEmpty()) {
            return Collections.emptyList();
        }

        final ImmutableMap<String, Object> params = ImmutableMap.of("dimensions", dimensions);

        return template.query("" +
                "  SELECT id, schema, relation, description" +
                "    FROM dimension" +
                "   WHERE id IN (:dimensions)" +
                "ORDER BY priority ASC", params, mapRow());
    }

    @Override
    public List<Dimension> findAll() {
        return template.query("" +
                "  SELECT id, schema, relation, description" +
                "    FROM dimension " +
                "ORDER BY priority ASC", ImmutableMap.of(), mapRow());
    }

    @Hack
    private RowMapper<Dimension> mapRow() {
        return throwingBiFunction(this::map)::apply;
    }

    private Dimension map(final ResultSet row, @SuppressWarnings("unused") final int rowNum) throws Exception {
        return new Dimension(
                row.getString("id"),
                mapper.readTree(row.getBytes("schema")),
                row.getString("relation"),
                row.getString("description"));
    }

    @Override
    public boolean update(final Dimension dimension) throws IOException {
        final ImmutableMap<String, Object> params = ImmutableMap.of(
                "id", dimension.getId(),
                "schema", mapper.writeValueAsString(dimension.getSchema()),
                "relation", dimension.getRelation(),
                "description", dimension.getDescription());

        final int updates = template.update("" +
                "UPDATE dimension" +
                "   SET schema = :schema::JSONB," +
                "       relation = :relation," +
                "       description = :description" +
                " WHERE id = :id", params);

        return updates > 0;
    }

    // TODO express this more generically?!
    public void reorder(final List<String> dimensions) {
        final List<Object[]> ranks = new ArrayList<>(dimensions.size());
        final ListIterator<String> iterator = dimensions.listIterator();

        while (iterator.hasNext()) {
            ranks.add(new Object[]{iterator.next(), iterator.nextIndex()});
        }

        template.update("" +
                "UPDATE dimension" +
                "   SET priority = new_priority" +
                "  FROM (VALUES :ranks) AS ranks(dimension_id, new_priority)" +
                " WHERE id = dimension_id", ImmutableMap.of("ranks", ranks));
    }

    @Override
    public void delete(final String dimension) {
        final int deletions = template.update("" +
                "DELETE" +
                "  FROM dimension" +
                " WHERE id = :id", ImmutableMap.of("id", dimension));

        if (deletions == 0) {
            throw new NotFoundException();
        }
    }

}
