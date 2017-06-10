package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Service
class DefaultValueService implements ValueService {

    private final CreateValue create;
    private final ReadValue readOne;
    private final ReadValuesByKeyAndFilter readManyByKeyAndFilter;
    private final ReadValuesByKey readManyByKey;
    private final ReadValuesByDimension readManyByDimension;
    private final ReadAllValues readAll;
    private final DeleteValue delete;

    @Autowired
    DefaultValueService(final CreateValue create, final ReadValue readOne,
            final ReadValuesByKeyAndFilter readManyByKeyAndFilter, final ReadValuesByKey readManyByKey,
            final ReadValuesByDimension readManyByDimension, final ReadAllValues readAll, final DeleteValue delete) {
        this.create = create;
        this.readOne = readOne;
        this.readManyByKeyAndFilter = readManyByKeyAndFilter;
        this.readManyByKey = readManyByKey;
        this.readManyByDimension = readManyByDimension;
        this.readAll = readAll;
        this.delete = delete;
    }

    @Override
    public void create(final Value value) {
        create.create(value);
    }

    // TODO replace? update?

    @Override
    public Value read(final String key, final Map<String, JsonNode> filter) {
        return readOne.read(key, filter);
    }

    @Override
    public List<Value> readAllByKey(final String key, final Map<String, JsonNode> filter) {
        return readManyByKeyAndFilter.read(key, filter);
    }

    @Override
    public List<Value> readAllByKey(final String key) {
        return readManyByKey.read(key);
    }

    @Override
    public List<Value> readAllByDimension(final String dimension) {
        return readManyByDimension.read(dimension);
    }

    @Override
    public ListMultimap<String, Value> readAllByKeyPattern(@Nullable final String keyPattern) {
        return readAll.readAll(keyPattern);
    }

    @Override
    public void delete(final String key, final Map<String, JsonNode> filter) {
        delete.delete(key, filter);
    }

}
