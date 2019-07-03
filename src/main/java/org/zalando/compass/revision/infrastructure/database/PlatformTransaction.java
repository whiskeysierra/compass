package org.zalando.compass.revision.infrastructure.database;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.zalando.compass.revision.domain.spi.repository.Transaction;

import java.util.function.Supplier;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
class PlatformTransaction implements Transaction {

    private final TransactionTemplate template;

    @Override
    public <T> T execute(final Supplier<T> task) {
        return template.execute(status -> task.get());
    }

}
