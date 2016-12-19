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

import static com.google.common.collect.Ordering.explicit;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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
        final List<Value> values = readAll(key, filter).getValues();

        if (values.isEmpty()) {
            throw new NotFoundException();
        }

        return values.get(0);
    }

    public Values readAll(final String key, final Map<String, String> filter) {
        final List<Value> values = valueRepository.readAll(key);

        final List<Dimension> universe = dimensionRepository.readAll();

        final List<String> dimensionsInOrder = universe.stream()
                .map(Dimension::getId).collect(Collectors.toList());

        values.sort(comparing(this::byDimensions,
                explicit(dimensionsInOrder).reverse().lexicographical().reverse())
                .thenComparing((l, r) -> {
                    for (final String dimension : dimensionsInOrder) {
                        if (l.getDimensions().containsKey(dimension) && r.getDimensions().containsKey(dimension)) {
                            final Object left = l.getDimensions().get(dimension);
                            final Object right = r.getDimensions().get(dimension);

                            // TODO don't use toString
                            final int result = left.toString().compareTo(right.toString());

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

        final Set<String> relations2 = universe.stream()
                .map(Dimension::getRelation)
                .collect(toSet());

        final Map<String, Relation> relations = relationService.readAll().stream()
                .collect(toMap(Relation::getId, identity()));

        // TODO apply algorithm based on relation

        return new Values(values);
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
