package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.zalando.twintip.spring.SchemaResource;

import static java.util.Arrays.asList;

@Configuration
@Import(SchemaResource.class)
public class WebConfiguration {

    @Bean
    public HttpMessageConverters messageConverters(final ObjectMapper mapper, final StringHttpMessageConverter textConverter) {
        return new HttpMessageConverters(false, asList(
                textConverter,
                new MappingJackson2HttpMessageConverter(mapper),
                new Jaxb2RootElementHttpMessageConverter()));
    }

}
