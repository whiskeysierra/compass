package org.zalando.compass.core.domain.spi.repository;

import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DimensionRepository {

    void create(Dimension dimension);

    List<Dimension> findAll(Set<String> dimensions);

    List<Dimension> findAll(@Nullable String term, Pagination<String> query);

    Optional<Dimension> find(String id);

    void update(Dimension dimension);

    void delete(Dimension dimension);

}
