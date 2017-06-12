package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Service
class DefaultValueService implements ValueService {

    private final ReplaceValue replace;
    private final ReplaceValues replaceMany;
    private final ReadValue readOne;
    private final ReadValuesByKeyAndFilter readManyByKeyAndFilter;
    private final ReadAllValues readAll;
    private final DeleteValue delete;

    @Autowired
    DefaultValueService(final ReplaceValue replace, final ReplaceValues replaceMany, final ReadValue readOne,
            final ReadValuesByKeyAndFilter readManyByKeyAndFilter, final ReadAllValues readAll,
            final DeleteValue delete) {
        this.replace = replace;
        this.replaceMany = replaceMany;
        this.readOne = readOne;
        this.readManyByKeyAndFilter = readManyByKeyAndFilter;
        this.readAll = readAll;
        this.delete = delete;
    }

    @Override
    public boolean replace(final String key, final Value value) {
        return replace.replace(key, value);
    }

    @Override
    public void replace(final String key, final List<Value> values) {
        replaceMany.replace(key, values);
    }

    @Override
    public Value read(final String key, final Map<String, JsonNode> filter) {
        return readOne.read(key, filter);
    }

    @Override
    public List<Value> readAllByKey(final String key, final Map<String, JsonNode> filter) {
        return readManyByKeyAndFilter.read(key, filter);
    }

    @Override
    public Map<Key, List<Value>> readAllByKeyPattern(@Nullable final String keyPattern) {
        return readAll.readAll(keyPattern);
    }

    @Override
    public void delete(final String key, final Map<String, JsonNode> filter) {
        delete.delete(key, filter);
    }

}
