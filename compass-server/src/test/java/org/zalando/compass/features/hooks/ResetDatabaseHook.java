package org.zalando.compass.features.hooks;

import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ResetDatabaseHook {


    private final JdbcTemplate template;

    @Autowired
    public ResetDatabaseHook(final JdbcTemplate template) {
        this.template = template;
    }

    @Before
    public void begin() {
        template.execute("TRUNCATE value CASCADE");
        template.execute("TRUNCATE dimension CASCADE");
        template.execute("TRUNCATE key CASCADE");
    }

}
