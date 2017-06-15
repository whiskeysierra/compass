package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.zalando.compass.domain.logic.relation.Equality;
import org.zalando.compass.domain.logic.relation.GreaterThanOrEqual;
import org.zalando.compass.domain.logic.relation.LessThanOrEqual;
import org.zalando.compass.domain.logic.relation.PrefixMatch;
import org.zalando.compass.domain.logic.relation.RegularExpression;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.Schema.stringSchema;

public class ValueMatcherTest {
    
    private final RichDimension after = new RichDimension("after", stringSchema(), new GreaterThanOrEqual(), "");
    private final RichDimension before = new RichDimension("before", stringSchema(), new LessThanOrEqual(), "");
    private final RichDimension country = new RichDimension("country", stringSchema(), new Equality(), "");
    private final RichDimension postalCode = new RichDimension("postalCode", stringSchema(), new Equality(), "");
    private final RichDimension locale = new RichDimension("locale", stringSchema(), new PrefixMatch(), "");
    private final RichDimension email = new RichDimension("email", stringSchema(), new RegularExpression(), "");

    private final List<RichValue> values = ImmutableList.of(
            new RichValue(of(country, text("CH"), before, text("2014-01-01T00:00:00Z")), decimal(0.05)),
            new RichValue(of(country, text("CH"), before, text("2015-01-01T00:00:00Z")), decimal(0.06)),
            new RichValue(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), decimal(0.09)),
            new RichValue(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), decimal(0.08)),
            new RichValue(of(country, text("DE"), after, text("2018-01-01T00:00:00Z")), decimal(0.22)),
            new RichValue(of(country, text("DE"), after, text("2017-01-01T00:00:00Z")), decimal(0.2)),
            new RichValue(of(country, text("DE"), postalCode, text("27498")), decimal(0.0)),
            new RichValue(of(country, text("CH")), decimal(0.07)),
            new RichValue(of(country, text("DE")), decimal(0.19)),
            new RichValue(of(after, text("2017-01-01T00:00:00Z")), decimal(0.5)),
            new RichValue(of(locale, text("de-DE")), decimal(0.19)),
            new RichValue(of(locale, text("en-DE")), decimal(0.18)),
            new RichValue(of(locale, text("de")), decimal(0.10)),
            new RichValue(of(email, text(".*@zalando\\.de")), decimal(0.0)),
            new RichValue(of(email, text(".*@goldmansachs\\.com")), decimal(1.0)),
            new RichValue(of(), decimal(0.25))
    );

    private final ValueMatcher unit = new ValueMatcher();

    @Test
    public void shouldMatchEquality() throws IOException {
        assertThat(unit.match(values, of(country, text("DE"))), contains(
                new RichValue(of(country, text("DE")), decimal(0.19)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchEqualityFallback() throws IOException {
        assertThat(unit.match(values, of(country, text("UK"))), contains(
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchLessThan() throws IOException {
        assertThat(unit.match(values, of(country, text("CH"), before, text("2013-12-20T11:47:19Z"))), contains(
                new RichValue(of(country, text("CH"), before, text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new RichValue(of(country, text("CH"), before, text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new RichValue(of(country, text("CH")), decimal(0.07)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchLessThanEqual() throws IOException {
        assertThat(unit.match(values, of(country, text("CH"), before, text("2014-01-01T00:00:00Z"))), contains(
                new RichValue(of(country, text("CH"), before, text("2014-01-01T00:00:00Z")), decimal(0.05)),
                new RichValue(of(country, text("CH"), before, text("2015-01-01T00:00:00Z")), decimal(0.06)),
                new RichValue(of(country, text("CH")), decimal(0.07)),
                new RichValue(of(), decimal(0.25))
        ));
    }

    @Test
    public void shouldMatchGreaterThan() throws IOException {
        assertThat(unit.match(values, of(country, text("CH"), after, text("2019-12-20T11:47:19Z"))), contains(
                new RichValue(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new RichValue(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new RichValue(of(country, text("CH")), decimal(0.07)),
                new RichValue(of(after, text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchGreaterThanEqual() throws IOException {
        assertThat(unit.match(values, of(country, text("CH"), after, text("2018-01-01T00:00:00Z"))), contains(
                new RichValue(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new RichValue(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new RichValue(of(country, text("CH")), decimal(0.07)),
                new RichValue(of(after, text("2017-01-01T00:00:00Z")), decimal(0.5)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchPrefix() throws IOException {
        assertThat(unit.match(values, of(locale, text("de-AT"))),contains(
                new RichValue(of(locale, text("de")), decimal(0.10)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchMatches() throws IOException {
        assertThat(unit.match(values, of(email, text("user@zalando.de"))), contains(
                new RichValue(of(email, text(".*@zalando\\.de")), decimal(0.0)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithoutFilter() throws IOException {
        assertThat(unit.match(values, of()), contains(
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithUnknownDimensions() throws IOException {
        final RichDimension foo = new RichDimension("foo", stringSchema(), new Equality(), "");
        assertThat(unit.match(values, of(foo, text("bar"))), contains(
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithoutMatchingDimensions() throws IOException {
        assertThat(unit.match(values, of(postalCode, text("12345"))), contains(
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithPartiallyUnknownDimensions() throws IOException {
        final RichDimension foo = new RichDimension("foo", stringSchema(), new Equality(), "");
        assertThat(unit.match(values, of(country, text("DE"),
                foo, text("bar"))), contains(
                new RichValue(of(country, text("DE")), decimal(0.19)),
                new RichValue(of(), decimal(0.25))));
    }

    @Test
    public void shouldMatchWithoutDimensionValues() {
        assertThat(unit.match(values, of(country, nullNode(), after, nullNode())), contains(
                new RichValue(of(country, text("CH"), after, text("2018-01-01T00:00:00Z")), decimal(0.09)),
                new RichValue(of(country, text("CH"), after, text("2017-01-01T00:00:00Z")), decimal(0.08)),
                new RichValue(of(country, text("DE"), after, text("2018-01-01T00:00:00Z")), decimal(0.22)),
                new RichValue(of(country, text("DE"), after, text("2017-01-01T00:00:00Z")), decimal(0.2))));
    }

    private JsonNode text(final String text) {
        return new TextNode(text);
    }

    private JsonNode decimal(final double v) {
        return new DecimalNode(new BigDecimal(v));
    }

    private JsonNode nullNode() {
        return NullNode.getInstance();
    }

}