package org.zalando.compass.domain.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zalando.compass.domain.persistence.model.tables.daos.DimensionDao;
import org.zalando.compass.domain.persistence.model.tables.daos.KeyDao;
import org.zalando.compass.library.jooq.DaoPostProcessor;

@Configuration
@Import({
        DimensionDao.class,
        KeyDao.class,
})
public class PersistenceConfiguration {
}
