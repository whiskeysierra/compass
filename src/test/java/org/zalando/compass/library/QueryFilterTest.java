package org.zalando.compass.library;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class QueryFilterTest {

    private final QueryFilter unit = new QueryFilter(ImmutableSet.of("a", "b", "c"));

    @Test
    public void shouldIgnoreQueryWithoutReservedKeys() {
        assertThat(unit.filter(singletonMap("foo", "bar")), is(singletonMap("foo", "bar")));
    }

    @Test
    public void shouldPartiallyFilterQueryWithReservedKeys() {
        assertThat(unit.filter(ImmutableMap.of("foo", "bar", "a", "1")), is(singletonMap("foo", "bar")));
    }

    @Test
    public void shouldFullyFilterQueryWithReservedKeys() {
        assertThat(unit.filter(ImmutableMap.of("a", "1", "b", "2")), is(emptyMap()));
    }

}