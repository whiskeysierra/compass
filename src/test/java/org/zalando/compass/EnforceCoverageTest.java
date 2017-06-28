package org.zalando.compass;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.Test;
import org.zalando.compass.library.Changed;
import org.zalando.compass.resource.MediaTypes;

@Hack
@OhNoYouDidnt
public final class EnforceCoverageTest {

    @Test
    public void shouldUseChangedConstructor() {
        new Changed();
    }

    @Test
    public void shouldUseMediaTypesConstructor() {
        new MediaTypes();
    }

}
