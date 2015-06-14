package org.zalando.compass.client;

/*
 * ⁣​
 * Compass Client
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.io.Resources;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.zalando.compass.api.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;

public final class NodeReaderTest {
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final NodeReader unit = new NodeReader(mapper);

    public NodeReaderTest() {
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new GuavaModule());
    }
    
    @Test
    public void shouldRead() throws IOException {
        try (InputStream stream = Resources.getResource("node.json").openStream()) {
            final Node<InetAddress> node = unit.read(stream, InetAddress.class);
            
            assertThat(node, is(notNullValue()));
            assertThat(node.getDimension(), isPresent());
            assertThat(node.getDimension().get(), is(URI.create("http://docs.aws.amazon.com/regions")));
            assertThat(node.getValues().keySet(), contains("eu-west-1", "eu-central-1"));
            assertThat(node.getValue(), isAbsent());

            final Node<InetAddress> ireland = node.getValues().get("eu-west-1");
            
            
            final Node<InetAddress> frankfurt = node.getValues().get("eu-central-1");


            // TODO assert that nodes is never null, only empty
        }
    }

    private <T> Matcher<Optional<T>> isPresent() {
        return hasFeature("is present", "present", Optional::isPresent, is(true));
    }
    
    private <T> Matcher<Optional<T>> isAbsent() {
        return hasFeature("is present", "present", Optional::isPresent, is(false));
    }

}