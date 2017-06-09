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
    private final ReadValues readMany;
    private final ReadAllValues readAll;
    private final DeleteValue delete;

    @Autowired
    DefaultValueService(final CreateValue create, final ReadValue readOne,
            final ReadValues readMany, final ReadAllValues readAll,
            final DeleteValue delete) {
        this.create = create;
        this.readOne = readOne;
        this.readMany = readMany;
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
        return readMany.read(key, filter);
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
