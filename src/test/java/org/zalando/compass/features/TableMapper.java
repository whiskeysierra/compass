package org.zalando.compass.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.gag.annotation.remark.Hack;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zuchini.runner.tables.Datatable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static com.jayway.jsonpath.JsonPath.parse;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.zalando.fauxpas.FauxPas.throwingConsumer;
import static org.zalando.fauxpas.FauxPas.throwingFunction;

@Component
public class TableMapper {

    private final Configuration configuration = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

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
        final DocumentContext context = parse(node, configuration);

        return headers.stream()
                .collect(toMap(identity(), header ->
                        context.read(toJsonPath(header)).toString()));
    }

    public List<JsonNode> map(final Datatable table) throws IOException {
        return table.toMap().stream()
                .map(throwingFunction(this::map))
                .collect(toList());
    }

    // TODO https://github.com/jayway/JsonPath/issues/83
    @Hack("Only supports dot notation; no brackets or arrays")
    private JsonNode map(final Map<String, String> row) throws IOException {
        final JsonNode node = mapper.readTree("{}");

        final DocumentContext context = parse(node, configuration);

        row.keySet().forEach(throwingConsumer(header -> {
            final List<String> paths = Splitter.on('.').splitToList(toJsonPath(header));
            final int size = paths.size();

            if (size == 2) {
                context.put("$", header, mapper.readTree(row.get(header)));
            } else {
                for (int i = 1; i < size - 1; i++) {
                    try {
                        context.read(join(paths.subList(0, i + 1)));
                    } catch (final PathNotFoundException e) {
                        final String path = join(paths.subList(0, i));
                        final String property = paths.get(i);
                        context.put(path, property, new HashMap<>());
                    }
                }

                final int last = size - 1;
                final List<String> path = paths.subList(0, last);
                final String property = paths.get(last);
                context.put(join(path), property, mapper.readTree(row.get(header)));
            }
        }));

        return node;
    }

    private String toJsonPath(final String header) {
        return "$." + header;
    }

    private String join(final List<String> list) {
        return on('.').join(list);
    }

}
