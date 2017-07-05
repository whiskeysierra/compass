package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.util.List;

@Service
class DefaultKeyService implements KeyService {

    private final ReplaceKey replace;
    private final ReadKey read;
    private final DeleteKey delete;

    @Autowired
    DefaultKeyService(final ReplaceKey replace, final ReadKey read, final DeleteKey delete) {
        this.replace = replace;
        this.read = read;
        this.delete = delete;
    }

    @Transactional
    @Override
    public boolean replace(final Key key) {
        return replace.replace(key);
    }

    @Override
    public Key read(final String id) {
        return read.read(id);
    }

    @Override
    public Page<Revision> readRevisions(final int limit, @Nullable final Long after) {
        return read.readRevisions(limit, after);
    }

    @Override
    public List<Key> readRevision(final long revision) {
        return read.readRevision(revision);
    }

    @Override
    public List<Key> readAll(@Nullable final String term) {
        return read.readAll(term);
    }

    @Override
    public Page<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return read.readRevisions(id, limit, after);
    }

    @Override
    public KeyRevision readRevision(final String id, final long revision) {
        return read.readRevision(id, revision);
    }

    @Transactional
    @Override
    public void delete(final String id) {
        delete.delete(id);
    }

}
