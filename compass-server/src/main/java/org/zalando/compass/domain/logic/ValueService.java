package org.zalando.compass.domain.logic;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Relation;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Ordering.explicit;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class ValueService {

    private final ValueRepository valueRepository;
    private final DimensionRepository dimensionRepository;
    private final RelationService relationService;

    @Autowired
    public ValueService(final ValueRepository valueRepository, final DimensionRepository dimensionRepository,
            final RelationService relationService) {
        this.valueRepository = valueRepository;
        this.dimensionRepository = dimensionRepository;
        this.relationService = relationService;
    }

    public void createOrUpdate(final String key, final Values values) {
        valueRepository.create(key, values.getValues());
    }

    public Value read(final String key, final Map<String, String> filter) {
        return readAll(key, filter).getValues().stream()
                .findFirst().orElseThrow(NotFoundException::new);
    }

    public Values readAll(final String key, final Map<String, String> filter) {
        final List<Value> values = valueRepository.readAll(key);

        final List<Dimension> universe = dimensionRepository.readAll();

        final Map<String, Dimension> lookup = universe.stream().collect(toMap(Dimension::getId, identity()));

        final List<String> inOrder = universe.stream()
                .map(Dimension::getId).collect(Collectors.toList());

        final Map<String, Relation> relations = relationService.readAll().stream()
                .collect(toMap(Relation::getId, identity()));

        values.sort(comparing(this::byDimensions, comparing(Set<String>::size).reversed())
                .thenComparing(comparing(this::byDimensions, explicit(inOrder).reverse().lexicographical().reverse()))
                .thenComparing((l, r) -> {
                    for (final String dimension : inOrder) {
                        if (l.getDimensions().containsKey(dimension) && r.getDimensions().containsKey(dimension)) {
                            final Object left = l.getDimensions().get(dimension);
                            final Object right = r.getDimensions().get(dimension);
                            final Relation relation = relations.get(lookup.get(dimension).getRelation());

                            // TODO don't use toString
                            final int result = relation.compare(left.toString(), right.toString());

                            if (result != 0) {
                                return result;
                            }
                        }
                    }

                    return 0;
                }));

        if (filter.isEmpty()) {
            return new Values(values);
        }

        return values.stream()
                .filter(value -> {
                    for (String dimension : inOrder) {
                        final boolean isConfigured = value.getDimensions().containsKey(dimension);
                        final boolean isRequested = filter.containsKey(dimension);

                        if (isConfigured) {
                            if (!isRequested) {
                                return false;
                            }

                            final String configured = value.getDimensions().get(dimension).toString();
                            final String requested = filter.get(dimension);
                            final Relation relation = relations.get(lookup.get(dimension).getRelation());

                            if (!relation.test(configured, requested)) {
                                return false;
                            }
                        }
                    }

                    return true;
                })
                .collect(collectingAndThen(toList(), Values::new));
    }

    private ImmutableSet<String> byDimensions(final Value value) {
        return value.getDimensions().keySet();
    }

    @Transactional
    public void replace(final String key, final Values values) throws IOException {
        delete(key, emptyMap());
        createOrUpdate(key, values);
    }

    public void delete(final String key, final Map<String, Object> filter) throws IOException {
        valueRepository.delete(key, filter);
    }

}
