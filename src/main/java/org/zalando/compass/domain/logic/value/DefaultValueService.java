package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Service
class DefaultValueService implements ValueService {

    private final ReplaceValue replace;
    private final ReadValue read;
    private final ReadValueRevision readRevision;
    private final DeleteValue delete;

    @Autowired
    DefaultValueService(final ReplaceValue replace, final ReadValue read,
            final ReadValueRevision readRevision, final DeleteValue delete) {
        this.replace = replace;
        this.read = read;
        this.readRevision = readRevision;
        this.delete = delete;
    }

    @Transactional
    @Override
    public boolean replace(final String key, final List<Value> values, @Nullable final String comment) {
        return replace.replace(key, values, comment);
    }

    @Transactional
    @Override
    public boolean replace(final String key, final Value value, @Nullable final String comment) {
        return replace.replace(key, value, comment);
    }

    @Override
    public List<Value> readPage(final String key, final Map<String, JsonNode> filter) {
        return read.readAll(key, filter);
    }

    @Override
    public Value read(final String key, final Map<String, JsonNode> filter) {
        return read.read(key, filter);
    }

    @Override
    public List<Revision> readPageRevisions(final String key) {
        return readRevision.readPageRevisions(key);
    }

    @Override
    public PageRevision<Value> readPageAt(final String key, final Map<String, JsonNode> filter, final long revision) {
        return readRevision.readPageAt(key, filter, revision);
    }

    @Override
    public List<Revision> readRevisions(final String key, final Map<String, JsonNode> dimensions) {
        return readRevision.readRevisions(key, dimensions);
    }

    @Override
    public ValueRevision readAt(final String key, final Map<String, JsonNode> dimensions, final long revision) {
        return readRevision.readAt(key, dimensions, revision);
    }

    @Transactional
    @Override
    public void delete(final String key, final Map<String, JsonNode> filter, @Nullable final String comment) {
        delete.delete(key, filter, comment);
    }

}
