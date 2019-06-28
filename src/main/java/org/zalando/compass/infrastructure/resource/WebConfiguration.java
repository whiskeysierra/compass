package org.zalando.compass.infrastructure.resource;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

import static com.atlassian.oai.validator.report.ValidationReport.Level.IGNORE;
import static java.util.Arrays.asList;

@Configuration
class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new OpenApiValidationInterceptor(OpenApiInteractionValidator
                .createFor("/api/api.yaml")
                .withLevelResolver(
                        LevelResolver.create()
                                // This is needed if your spec uses composition via {@code allOf}, {@code anyOf} or {@code oneOf}.
                                .withLevel("validation.schema.additionalProperties", IGNORE)
                                // This is needed because we use dynamic query parameters
                                .withLevel("validation.request.parameter.query.unexpected", IGNORE)
                                .build())
                .build()));
    }

    @Bean
    public Filter validationFilter() {
        return new OpenApiValidationFilter(
                true, // enable request validation
                true  // enable response validation
        );
    }

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/*").addResourceLocations("classpath:/api/");
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
