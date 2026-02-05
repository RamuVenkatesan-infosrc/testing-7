package com.banking.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ServiceConfig serviceConfig;

    @Value("${allowed.origins}")
    private String[] allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(getAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    private String[] getAllowedOrigins() {
        List<String> origins = Arrays.asList(allowedOrigins);
        origins.addAll(Arrays.asList(serviceConfig.getTrustedOrigins()));
        return origins.toArray(new String[0]);
    }

    @Bean
    public HeaderWriter contentSecurityPolicyHeaderWriter() {
        return new StaticHeadersWriter("Content-Security-Policy",
                "default-src 'self'; script-src 'self'; " +
                "style-src 'self'; img-src 'self' data:; " +
                "font-src 'self'; connect-src 'self'; frame-src 'none'; " +
                "object-src 'none'; base-uri 'self';");
    }
}