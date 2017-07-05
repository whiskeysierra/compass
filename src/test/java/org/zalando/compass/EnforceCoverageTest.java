package org.zalando.compass;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.Test;
import org.zalando.compass.library.Changed;
import org.zalando.compass.library.Enums;
import org.zalando.compass.library.Maps;
import org.zalando.compass.library.Pages;
import org.zalando.compass.library.Tables;
import org.zalando.compass.resource.MediaTypes;

import java.util.Map;

@Hack
@OhNoYouDidnt
public final class EnforceCoverageTest {

    @Test
    public void shouldUseChangedConstructor() {
        new Changed();
    }

    @Test
    public void shouldUseEnumsConstructor() {
        new Enums();
    }

    @Test
    public void shouldUseMapsConstructor() {
        new Maps();
    }

    @Test
    public void shouldUseMediaTypesConstructor() {
        new MediaTypes();
    }

    @Test
    public void shouldUsePagesConstructor() {
        new Pages();
    }

    @Test
    public void shouldUseTablesConstructor() {
        new Tables();
    }

}
