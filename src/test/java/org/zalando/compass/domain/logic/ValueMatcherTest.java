package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.zalando.compass.domain.logic.relation.Equality;
import org.zalando.compass.domain.logic.relation.GreaterThanOrEqual;
import org.zalando.compass.domain.logic.relation.LessThanOrEqual;
import org.zalando.compass.domain.logic.relation.PrefixMatch;
import org.zalando.compass.domain.logic.relation.RegularExpression;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.Schema.stringSchema;

// TODO should test without concrete relation implementations!
public class ValueMatcherTest {
    
    private final RichDimension after = new RichDimension("after", stringSchema(), new GreaterThanOrEqual(), "");
    private final RichDimension before = new RichDimension("before", stringSchema(), new LessThanOrEqual(), "");
    private final RichDimension country = new RichDimension("country", stringSchema(), new Equality(), "");
    private final RichDimension postalCode = new RichDimension("postalCode", stringSchema(), new Equality(), "");
    private final RichDimension locale = new RichDimension("locale", stringSchema(), new PrefixMatch(), "");
    private final RichDimension email = new RichDimension("email", stringSchema(), new RegularExpression(), "");

    private final List<Map<RichDimension, JsonNode>> values = ImmutableList.of(
            of(country, text("CH"), before, text("2014-01-01T00:00:00Z")),
            of(country, text("CH"), before, text("2015-01-01T00:00:00Z")),
            of(country, text("CH"), after, text("2018-01-01T00:00:00Z")),
            of(country, text("CH"), after, text("2017-01-01T00:00:00Z")),
            of(country, text("DE"), after, text("2018-01-01T00:00:00Z")),
            of(country, text("DE"), after, text("2017-01-01T00:00:00Z")),
            of(country, text("DE"), postalCode, text("27498")),
            of(country, text("CH")),
            of(country, text("DE")),
            of(after, text("2017-01-01T00:00:00Z")),
            of(locale, text("de-DE")),
            of(locale, text("en-DE")),
            of(locale, text("de")),
            of(email, text(".*@zalando\\.de")),
            of(email, text(".*@goldmansachs\\.com")),
            of()
    );

    private final ValueMatcher unit = new ValueMatcher();

    @Test
    public void shouldMatchEquality() {
        assertThat(unit.match(values, of(country, text("DE"))), is(asList(
                of(country, text("DE")),
                of())));
    }

    @Test
    public void shouldMatchEqualityFallback() {
        assertThat(unit.match(values, of(country, text("UK"))), is(singletonList(of())));
    }

    @Test
    public void shouldMatchLessThan() {
        assertThat(unit.match(values, of(country, text("CH"), before, text("2013-12-20T11:47:19Z"))), is(asList(
                of(country, text("CH"), before, text("2014-01-01T00:00:00Z")),
                of(country, text("CH"), before, text("2015-01-01T00:00:00Z")),
                of(country, text("CH")),
                of())));
    }

    @Test
    public void shouldMatchLessThanEqual() {
        assertThat(unit.match(values, of(country, text("CH"), before, text("2014-01-01T00:00:00Z"))), is(asList(
                of(country, text("CH"), before, text("2014-01-01T00:00:00Z")),
                of(country, text("CH"), before, text("2015-01-01T00:00:00Z")),
                of(country, text("CH")),
                of())));
    }

    @Test
    public void shouldMatchGreaterThan() {
        assertThat(unit.match(values, of(country, text("CH"), after, text("2019-12-20T11:47:19Z"))), is(asList(
                of(country, text("CH"), after, text("2018-01-01T00:00:00Z")),
                of(country, text("CH"), after, text("2017-01-01T00:00:00Z")),
                of(country, text("CH")),
                of(after, text("2017-01-01T00:00:00Z")),
                of())));
    }

    @Test
    public void shouldMatchGreaterThanEqual() {
        assertThat(unit.match(values, of(country, text("CH"), after, text("2018-01-01T00:00:00Z"))), is(asList(
                of(country, text("CH"), after, text("2018-01-01T00:00:00Z")),
                of(country, text("CH"), after, text("2017-01-01T00:00:00Z")),
                of(country, text("CH")),
                of(after, text("2017-01-01T00:00:00Z")),
                of())));
    }

    @Test
    public void shouldMatchPrefix() {
        assertThat(unit.match(values, of(locale, text("de-AT"))),is(asList(
                of(locale, text("de")),
                of())));
    }

    @Test
    public void shouldMatchMatches() {
        assertThat(unit.match(values, of(email, text("user@zalando.de"))), is(asList(
                of(email, text(".*@zalando\\.de")),
                of())));
    }

    @Test
    public void shouldMatchWithoutFilter() {
        assertThat(unit.match(values, of()), is(singletonList(of())));
    }

    @Test
    public void shouldMatchWithUnknownDimensions() {
        final RichDimension foo = new RichDimension("foo", stringSchema(), new Equality(), "");
        assertThat(unit.match(values, of(foo, text("bar"))), is(singletonList(of())));
    }

    @Test
    public void shouldMatchWithoutMatchingDimensions() {
        assertThat(unit.match(values, of(postalCode, text("12345"))), is(singletonList(of())));
    }

    @Test
    public void shouldMatchWithPartiallyUnknownDimensions() {
        final RichDimension foo = new RichDimension("foo", stringSchema(), new Equality(), "");
        assertThat(unit.match(values, of(country, text("DE"),
                foo, text("bar"))), is(asList(
                of(country, text("DE")),
                of())));
    }

    private JsonNode text(final String text) {
        return new TextNode(text);
    }

}
