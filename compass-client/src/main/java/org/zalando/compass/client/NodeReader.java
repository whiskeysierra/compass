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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.zalando.compass.api.Node;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public final class NodeReader {
    
    private final ObjectMapper mapper;

    public NodeReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> Node<T> read(InputStream stream, Class<T> type) throws IOException {
        return read(stream, TypeToken.of(type));
    }
    
    public <T> Node<T> read(InputStream stream, TypeToken<T> type) throws IOException {
        // TODO validate variants:
        // dimension+nodes+value
        // dimension+nodes+!value
        // !dimensions+!nodes+value
        
        return mapper.readValue(stream, new TypeReference<Node<T>>() {
            @Override
            public Type getType() {
                return new TypeToken<Node<T>>() {}
                        .where(new TypeParameter<T>() {}, type)
                        .getType();
            }
        });
    }

}
