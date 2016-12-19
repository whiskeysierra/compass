package org.zalando.compass.domain.logic;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.Values;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Ordering.explicit;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Service
public class ValueService {

    private final ValueRepository valueRepository;
    private final DimensionRepository dimensionRepository;

    @Autowired
    public ValueService(final ValueRepository valueRepository, final DimensionRepository dimensionRepository) {
        this.valueRepository = valueRepository;
        this.dimensionRepository = dimensionRepository;
    }

    public void createOrUpdate(final String key, final Values values) {
        valueRepository.create(key, values.getValues());
    }

    public Value read(final String key, final Map<String, String> filter) {
        @Nullable final Value value = singleResult(readAll(key, filter).getValues());

        if (value == null) {
            throw new NotFoundException();
        }

        return value;
    }

    public Values readAll(final String key, final Map<String, String> filter) {
        final List<Value> values = valueRepository.readAll(key);

        final List<Dimension> universe = dimensionRepository.readAll();

        final List<String> dimensionsInOrder = universe.stream()
                .map(Dimension::getId).collect(Collectors.toList());

        final Function<Value, ImmutableSet<String>> getDimensions = value -> value.getDimensions().keySet();

        values.sort(comparing(getDimensions,
                explicit(dimensionsInOrder).lexicographical()));

        if (filter.isEmpty()) {
            return new Values(values);
        }

        final Set<String> relations = universe.stream()
                .map(Dimension::getRelation)
                .collect(toSet());

        // TODO apply algorithm based on relation

        return new Values(values);
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
