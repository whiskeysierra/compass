package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

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

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public boolean replace(final Key key, @Nullable final String comment) {
        return replace.replace(key, comment);
    }

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public void create(final Key key, @Nullable final String comment) throws EntityAlreadyExistsException {
        replace.create(key, comment);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Key> readPage(@Nullable final String term, final Pagination<String> query) {
        return read.readPage(term, query);
    }

    @Transactional(readOnly = true)
    @Override
    public Revisioned<Key> read(final String id) {
        final Key key = readOnly(id);
        final Revision revision = readRevision.readLatestRevision(id);
        return Revisioned.create(key, revision);
    }

    @Transactional(readOnly = true)
    @Override
    public Key readOnly(final String id) {
        return read.read(id);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readPageRevisions(final Pagination<Long> query) {
        return readRevision.readPageRevisions(query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRevision<Key> readPageAt(final long revision, final Pagination<String> query) {
        return readRevision.readPageAt(revision, query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readRevisions(final String id, final Pagination<Long> query) {
        return readRevision.readRevisions(id, query);
    }

    @Transactional(readOnly = true)
    @Override
    public KeyRevision readAt(final String id, final long revision) {
        return readRevision.readAt(id, revision);
    }

    @Transactional // TODO isolation?!
    @Override
    public void delete(final String id, @Nullable final String comment) {
        delete.delete(id, comment);
    }

}
