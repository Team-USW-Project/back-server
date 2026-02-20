package com.uswProject.route.client;

import feign.RequestInterceptor;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class OdsayFeignConfig {

    @Value("${odsay.api-key}")
    private String apiKey;

    @Value("${odsay.lang:0}")
    private int lang;

    @Value("${odsay.output:json}")
    private String output;

    @Bean
    public RequestInterceptor odsayAuthInterceptor() {
        return requestTemplate -> {
            String encoded = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            requestTemplate.query("apiKey", encoded);
            requestTemplate.query("lang", String.valueOf(lang));
            requestTemplate.query("output", output);
        };
    }
}
