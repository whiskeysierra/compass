package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.util.List;

@Service
class DefaultKeyService implements KeyService {

    private final ReplaceKey replace;
    private final ReadKey read;
    private final ReadKeyRevision readRevision;
    private final DeleteKey delete;

    @Autowired
    DefaultKeyService(final ReplaceKey replace, final ReadKey read,
            final ReadKeyRevision readRevision, final DeleteKey delete) {
        this.replace = replace;
        this.read = read;
        this.readRevision = readRevision;
        this.delete = delete;
    }

    @Transactional
    @Override
    public boolean replace(final Key key) {
        return replace.replace(key);
    }

    @Override
    public List<Key> readPage(@Nullable final String term) {
        return read.readPage(term);
    }

    @Override
    public Key read(final String id) {
        return read.read(id);
    }

    @Override
    public Page<Revision> readPageRevisions(final int limit, @Nullable final Long after) {
        return readRevision.readPageRevisions(limit, after);
    }

    @Override
    public PageRevision<Key> readPageAt(final long revision) {
        return readRevision.readPageAt(revision);
    }

    @Override
    public Page<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return readRevision.readRevisions(id, limit, after);
    }

    @Override
    public KeyRevision readAt(final String id, final long revision) {
        return readRevision.readAt(id, revision);
    }

    @Transactional
    @Override
    public void delete(final String id) {
        delete.delete(id);
    }

}
