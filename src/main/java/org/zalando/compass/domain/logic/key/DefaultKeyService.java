package org.zalando.compass.domain.logic.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.KeyService;
import org.zalando.compass.domain.model.Key;

@Service
class DefaultKeyService implements KeyService {

    private final ReplaceKey replace;
    private final DeleteKey delete;

    @Autowired
    DefaultKeyService(final ReplaceKey replace, final DeleteKey delete) {
        this.replace = replace;
        this.delete = delete;
    }

    @Override
    public boolean replace(final Key key) {
        return replace.replace(key);
    }

    @Override
    public void delete(final String id) {
        delete.delete(id);
    }

}
