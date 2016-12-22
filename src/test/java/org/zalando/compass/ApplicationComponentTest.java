package org.zalando.compass;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Starts the application in-process with mocked external dependencies.
 * <strong>Beware</strong>: This test does <strong>NOT</strong> cleanup after itself, i.e. it leaves state in the
 * database. Since it's the only one of this kind that we have we should be fine...
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Component
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ApplicationComponentTest {

    @Test
    public void shouldStart() {
        // nothing to do.
    }

}
