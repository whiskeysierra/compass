package org.zalando.compass.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.spi.repository.lock.KeyLockRepository;
import org.zalando.compass.domain.spi.repository.lock.ValueLockRepository;

import javax.annotation.Nullable;
import java.util.List;

import static org.zalando.compass.domain.spi.repository.ValueCriteria.byKey;

/**
 * TODO this is actually a postgres/database concern and shouldn't be here:
 *
 * Centralizes locking of entities. In order to avoid deadlocks we ensure the following order of locks:
 *
 * <ol>
 *     <li>Dimensions</li>
 *     <li>Keys</li>
 *     <li>Values</li>
 * </ol>
 *
 * All ordered by id, respectively.
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class KeyLocking {

    private final KeyLockRepository keyLockRepository;
    private final ValueLockRepository valueLockRepository;

    KeyLock lock(final String id) {
        @Nullable final Key current = keyLockRepository.lock(id).orElse(null);
        final List<Value> values = valueLockRepository.lockAll(byKey(id));

        return new KeyLock(current, values);
    }

}
