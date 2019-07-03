package org.zalando.compass.revision.domain.spi.repository;

import java.util.function.Supplier;

public interface Transaction {

    default void execute(final Runnable task) {
        execute(() -> {
            task.run();
            return null;
        });
    }

    <T> T execute(Supplier<T> task);

}
