package org.zalando.compass.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public final class RelationTest {

    private final Relation unit;

    public RelationTest(final Relation unit) {
        this.unit = unit;
    }

    @Parameterized.Parameters(name= "{0}")
    public static Iterable<Object[]> data() {
        return stream(load(Relation.class).spliterator(), false)
                .map(relation -> new Object[] {relation})
                .collect(toList());
    }

    @Test
    public void toStringShouldMatchId() {
        assertThat(unit, hasToString(unit.getId()));
    }

}