package org.zalando.compass;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public final class WitherTest<T, P> {

    @Parameter
    public TestCase<T, P> test;

    @lombok.Value
    private static final class TestCase<T, P> {
        T instance;
        BiFunction<T, P, T> with;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<TestCase<?, ?>> data() {

        return Arrays.asList(
                new TestCase<>(new Revision(null, null, null, null, null), Revision::withId),
                new TestCase<>(new Revision(null, null, null, null, null), Revision::withType),
                new TestCase<>(new Value(null, null, null), Value::withDimensions),
                new TestCase<>(new Value(null, null, null), Value::withIndex),
                new TestCase<PageRevision<Object>, List<Object>>(new PageRevision<>(null, null), PageRevision::withElements),
                new TestCase<Page<Object>, List<Object>>(new Page<>(null, null), Page::withElements)
        );
    }

    @Test
    public void equalsContract() {
        assertThat(test.with.apply(test.instance, null), is(sameInstance(test.instance)));
    }

}