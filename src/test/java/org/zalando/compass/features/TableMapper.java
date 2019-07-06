package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.fauxpas.ThrowingBiFunction;
import org.zuchini.runner.tables.Datatable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.zalando.compass.library.JsonMutator.withAt;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

@Component
public class TableMapper {

    private final ObjectMapper mapper;

    @Autowired
    public TableMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Datatable map(final List<JsonNode> nodes, final List<String> header) throws IOException {
        return nodes.stream()
                .map(throwingFunction(node -> map(node, header)))
                .collect(collectingAndThen(toList(), rows -> Datatable.fromMaps(rows, header)));
    }

    public Map<String, String> map(final JsonNode node, final List<String> headers) throws IOException {
        return headers.stream()
                .collect(toMap(identity(), header -> node.at(header).toString()));
    }

    public List<JsonNode> map(final Datatable table) throws IOException {
        return table.toMap().stream()
                .map(throwingFunction(this::map))
                .collect(toList());
    }

    private JsonNode map(final Map<String, String> row) throws IOException {
        return row.keySet().stream()
                .reduce(null, withCell(row), (a, b) -> {
                    throw new UnsupportedOperationException();
                });
    }

    private ThrowingBiFunction<JsonNode, String, JsonNode, IOException> withCell(final Map<String, String> row) {
        return (node, header) -> {
            final var cell = row.get(header);
            final var value = cell.isEmpty() ? MissingNode.getInstance() : mapper.readTree(cell);

            // treats header as JSON Pointer
            return withAt(node, header, value);
        };
    }

}
