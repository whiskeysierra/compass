package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.zalando.compass.core.domain.model.relation.Equality;
import org.zalando.compass.core.domain.model.relation.GreaterThanOrEqual;
import org.zalando.compass.core.domain.model.relation.LessThanOrEqual;
import org.zalando.compass.core.domain.model.relation.PrefixMatch;
import org.zalando.compass.core.domain.model.relation.RegularExpression;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.Schema.stringSchema;

public class ValuesTest {

    private final Dimension after = new Dimension("after", stringSchema(), new GreaterThanOrEqual(), "");
    private final Dimension before = new Dimension("before", stringSchema(), new LessThanOrEqual(), "");
    private final Dimension country = new Dimension("country", stringSchema(), new Equality(), "");
    private final Dimension postalCode = new Dimension("postalCode", stringSchema(), new Equality(), "");
    private final Dimension locale = new Dimension("locale", stringSchema(), new PrefixMatch(), "");
    private final Dimension email = new Dimension("email", stringSchema(), new RegularExpression(), "");

    private final Values values = new Values(
            new Value(of(country, text("CH"), before, text("2014-01-01T00:00:00Z")), text("")),
            new Value(of(country, text("CH"), before, text("2015-01-01T00:00:00Z")), text("")),
            new Value(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), text("")),
            new Value(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), text("")),
            new Value(of(country, text("DE"), after, text("2018-01-01T00:00:00Z")), text("")),
            new Value(of(country, text("DE"), after, text("2017-01-01T00:00:00Z")), text("")),
            new Value(of(country, text("DE"), postalCode, text("27498")), text("")),
            new Value(of(country, text("CH")), text("")),
            new Value(of(country, text("DE")), text("")),
            new Value(of(after, text("2017-01-01T00:00:00Z")), text("")),
            new Value(of(locale, text("de-DE")), text("")),
            new Value(of(locale, text("en-DE")), text("")),
            new Value(of(locale, text("de")), text("")),
            new Value(of(email, text(".*@zalando\\.de")), text("")),
            new Value(of(email, text(".*@goldmansachs\\.com")), text("")),
            new Value(of(), text(""))
    );


    @Test
    public void shouldMatchEquality() {
        assertThat(values.select(of(country, text("DE"))), is(asList(
                new Value(of(country, text("DE")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchEqualityFallback() {
        assertThat(values.select(of(country, text("UK"))), is(singletonList(new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchLessThan() {
        assertThat(values.select(of(country, text("CH"), before, text("2013-12-20T11:47:19Z"))), is(asList(
                new Value(of(country, text("CH"), before, text("2014-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH"), before, text("2015-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchLessThanEqual() {
        assertThat(values.select(of(country, text("CH"), before, text("2014-01-01T00:00:00Z"))), is(asList(
                new Value(of(country, text("CH"), before, text("2014-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH"), before, text("2015-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchGreaterThan() {
        assertThat(values.select(of(country, text("CH"), after, text("2019-12-20T11:47:19Z"))), is(asList(
                new Value(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH")), text("")),
                new Value(of(after, text("2017-01-01T00:00:00Z")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchGreaterThanEqual() {
        assertThat(values.select(of(country, text("CH"), after, text("2018-01-01T00:00:00Z"))), is(asList(
                new Value(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), text("")),
                new Value(of(country, text("CH")), text("")),
                new Value(of(after, text("2017-01-01T00:00:00Z")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchPrefix() {
        assertThat(values.select(of(locale, text("de-AT"))),is(asList(
                new Value(of(locale, text("de")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchMatches() {
        assertThat(values.select(of(email, text("user@zalando.de"))), is(asList(
                new Value(of(email, text(".*@zalando\\.de")), text("")),
                new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchWithoutFilter() {
        assertThat(values.select(of()), is(singletonList(new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchWithUnknownDimensions() {
        final var foo = new Dimension("foo", stringSchema(), new Equality(), "");
        assertThat(values.select(of(foo, text("bar"))), is(singletonList(new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchWithoutMatchingDimensions() {
        assertThat(values.select(of(postalCode, text("12345"))), is(singletonList(new Value(of(), text("")))));
    }

    @Test
    public void shouldMatchWithPartiallyUnknownDimensions() {
        final var foo = new Dimension("foo", stringSchema(), new Equality(), "");
        assertThat(values.select(of(country, text("DE"), foo, text("bar"))), is(asList(
                new Value(of(country, text("DE")), text("")),
                new Value(of(), text("")))));
    }

    private JsonNode text(final String text) {
        return new TextNode(text);
    }

}
