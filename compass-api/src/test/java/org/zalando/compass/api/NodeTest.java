package org.zalando.compass.api;

/*
 * ⁣​
 * Compass API
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

public final class NodeTest {

    @Test
    public void toStringShouldBeValueIfLeaf() {
        assertThat(Node.valueOf(0.19), hasToString("0.19"));
    }

    @Test
    public void toStringShouldBeDimensionAndValuesIfBranch() {
        final Node<BigDecimal> unit = Node.of(
                new Dimension("country"),
                ImmutableMap.of(
                        "DE", Node.valueOf(new BigDecimal("0.19")),
                        "AT", Node.valueOf(new BigDecimal("0.20"))));
        
        assertThat(unit, hasToString("{country:{DE:0.19, AT:0.20}}"));
    }

    @Test
    public void toStringShouldBeDimensionValuesAndValueIfBranchWithLeaf() {
        final Node<BigDecimal> unit = Node.of(
                new Dimension("country"),
                ImmutableMap.of(
                        "DE", Node.valueOf(new BigDecimal("0.19")),
                        "AT", Node.valueOf(new BigDecimal("0.20"))),
                new BigDecimal("0.19"));
        
        assertThat(unit, hasToString("{country:{DE:0.19, AT:0.20}, default:0.19}"));
    }

    @Test
    public void toStringShouldNest() {
        final Node<BigDecimal> unit = Node.of(
                new Dimension("country"),
                ImmutableMap.of(
                        "DE", Node.of(new Dimension("zipcode"), ImmutableMap.of(
                                        "27498", Node.valueOf(new BigDecimal("0.00"))),
                                new BigDecimal("0.19")),
                        "AT", Node.valueOf(new BigDecimal("0.20"))),
                new BigDecimal("0.19"));

        assertThat(unit, hasToString("{country:{DE:{zipcode:{27498:0.00}, default:0.19}, AT:0.20}, default:0.19}"));

    }

}
