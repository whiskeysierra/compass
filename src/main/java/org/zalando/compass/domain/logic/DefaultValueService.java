package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.EntityAlreadyExistsException;
import org.zalando.compass.domain.ValueService;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultValueService implements ValueService {

    private final ReplaceValue replace;
    private final ReadValue read;
    private final DeleteValue delete;

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public boolean replace(final String key, final List<Value> values, @Nullable final String comment) {
        return replace.replace(key, values, comment);
    }

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public void create(final String key, final List<Value> values, @Nullable final String comment) {
        replace.create(key, values, comment);
    }

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public boolean replace(final String key, final Value value, @Nullable final String comment) {
        return replace.replace(key, value, comment);
    }

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public void create(final String key, final Value value, @Nullable final String comment) throws EntityAlreadyExistsException {
        replace.create(key, value, comment);
    }

    @Transactional(readOnly = true)
    @Override
    public Revisioned<List<Value>> readPage(final String key, final Map<String, JsonNode> filter) {
        return read.readPage(key, filter);
    }

    @Transactional(readOnly = true)
    @Override
    public Revisioned<Value> read(final String key, final Map<String, JsonNode> filter) {
        return read.read(key, filter);
    }

    @Transactional(readOnly = true)
    @Override
    public Value readOnly(final String key, final Map<String, JsonNode> filter) {
        return read.readOnly(key, filter);
    }

    @Transactional // TODO isolation?!
    @Override
    public void delete(final String key, final Map<String, JsonNode> filter, @Nullable final String comment) {
        delete.delete(key, filter, comment);
    }

}
