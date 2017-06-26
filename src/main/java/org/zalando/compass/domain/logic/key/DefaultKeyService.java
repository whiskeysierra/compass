package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;

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
    public List<Key> readAllByKeyPattern(@Nullable final String keyPattern) {
        return read.readAll(keyPattern);
    }

    @Transactional
    @Override
    public void delete(final String id) {
        delete.delete(id);
    }

}
