package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.api.ValueService;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.revision.domain.spi.repository.Transaction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultValueService implements ValueService {

    private final Transaction tx;
    private final ReplaceValue replace;
    private final ReadValue read;
    private final DeleteValue delete;

    @Override
    public boolean replace(final String key, final List<Value> values, @Nullable final String comment) {
        return tx.execute(() -> replace.replace(key, values, comment));
    }

    @Override
    public void create(final String key, final List<Value> values, @Nullable final String comment) {
        tx.execute(() -> replace.create(key, values, comment));
    }

    @Override
    public boolean replace(final String key, final Value value, @Nullable final String comment) {
        return tx.execute(() -> replace.replace(key, value, comment));
    }

    @Override
    public void create(final String key, final Value value, @Nullable final String comment) throws EntityAlreadyExistsException {
        tx.execute(() -> replace.create(key, value, comment));
    }

    @Override
    public Revisioned<List<Value>> readPage(final String key, final Map<Dimension, JsonNode> filter) {
        return tx.execute(() -> read.readPage(key, filter));
    }

    @Override
    public Revisioned<Value> read(final String key, final Map<Dimension, JsonNode> filter) {
        return tx.execute(() -> read.read(key, filter));
    }

    @Override
    public Value readOnly(final String key, final Map<Dimension, JsonNode> filter) {
        return tx.execute(() -> read.readOnly(key, filter));
    }

    @Override
    public void delete(final String key, final Map<Dimension, JsonNode> filter, @Nullable final String comment) {
        tx.execute(() -> delete.delete(key, filter, comment));
    }

}
