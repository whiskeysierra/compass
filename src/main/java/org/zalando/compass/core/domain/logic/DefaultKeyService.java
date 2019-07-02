package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.api.KeyService;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultKeyService implements KeyService {

    private final ReplaceKey replace;
    private final ReadKey read;
    private final DeleteKey delete;

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
        return read.read(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Key readOnly(final String id) {
        return read.readOnly(id);
    }

    @Transactional // TODO isolation?!
    @Override
    public void delete(final String id, @Nullable final String comment) {
        delete.delete(id, comment);
    }

}
