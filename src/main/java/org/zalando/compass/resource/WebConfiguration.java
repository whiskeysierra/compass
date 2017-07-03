package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zalando.twintip.spring.SchemaResource;

import static java.util.Arrays.asList;

@Configuration
@Import(SchemaResource.class)
class WebConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter(final ObjectMapper mapper) {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        converter.setDefaultCharset(null);
        return converter;
    }

    @Bean
    public HttpMessageConverters messageConverters(final StringHttpMessageConverter textConverter,
            final MappingJackson2HttpMessageConverter jsonConverter) {
        return new HttpMessageConverters(false, asList(textConverter, jsonConverter));
    }

}
