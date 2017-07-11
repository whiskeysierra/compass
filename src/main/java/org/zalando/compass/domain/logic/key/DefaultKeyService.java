package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

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
    public boolean replace(final Key key, @Nullable final String comment) {
        return replace.replace(key, comment);
    }

    @Override
    public PageResult<Key> readPage(@Nullable final String term, final PageQuery<String> query) {
        return read.readPage(term, query);
    }

    @Override
    public Key read(final String id) {
        return read.read(id);
    }

    @Override
    public PageResult<Revision> readPageRevisions(final PageQuery<Long> query) {
        return readRevision.readPageRevisions(query);
    }

    @Override
    public PageRevision<Key> readPageAt(final long revision, final PageQuery<String> query) {
        return readRevision.readPageAt(revision, query);
    }

    @Override
    public PageResult<Revision> readRevisions(final String id, final PageQuery<Long> query) {
        return readRevision.readRevisions(id, query);
    }

    @Override
    public KeyRevision readAt(final String id, final long revision) {
        return readRevision.readAt(id, revision);
    }

    @Transactional
    @Override
    public void delete(final String id, @Nullable final String comment) {
        delete.delete(id, comment);
    }

}
