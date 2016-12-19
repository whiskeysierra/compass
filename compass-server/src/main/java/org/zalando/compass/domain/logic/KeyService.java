package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Keys;
import org.zalando.compass.domain.persistence.KeyRepository;

import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.singleton;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Service
public class KeyService {

    private final KeyRepository repository;

    @Autowired
    public KeyService(final KeyRepository repository) {
        this.repository = repository;
    }

    public boolean createOrUpdate(final Key key) throws IOException {
        if (repository.create(key)) {
            return true;
        }

        repository.update(key);
        return false;
    }

    @Nullable
    public Key read(final String id) {
        final List<Key> keys = repository.read(singleton(id));

        @Nullable final Key key = singleResult(keys);

        if (key == null) {
            throw new NotFoundException();
        }

        return key;
    }

    public Keys readAll() {
        return new Keys(repository.readAll());
    }

    public void delete(final String id) {
        if (!repository.delete(id)) {
            throw new NotFoundException();
        }
    }

}
