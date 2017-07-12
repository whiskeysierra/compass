package org.zalando.compass;

import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;

import java.util.Arrays;
import java.util.function.BiFunction;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public final class WitherTest<T, P> {

    @Parameter
    public TestCase<T, P> test;

    @lombok.Value
    @AllArgsConstructor
    private static final class TestCase<T, P> {
        T instance;
        BiFunction<T, P, T> with;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<TestCase<?, ?>> data() {

        return Arrays.asList(
                new TestCase<>(new Revision(0L, null, null, null, null), Revision::withType),
                new TestCase<>(new Value(null, null, null), Value::withDimensions),
                new TestCase<>(new Value(null, null, null), Value::withIndex)
        );
    }

    @Test
    public void equalsContract() {
        assertThat(test.with.apply(test.instance, null), is(sameInstance(test.instance)));
    }

}
