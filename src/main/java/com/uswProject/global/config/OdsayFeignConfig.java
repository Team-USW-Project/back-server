package com.uswProject.global.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Configuration을 어노테이션으로 달면 전역 Bean으로 등록되어 다른 client 요청 시에도 문제가 발생하여 어노테이션 사용 안함
 * 또한 앞으로 API사용이 증가되면 파라미터를 계속 써줘야하기에, 편리함과 가독성 측면에서도 config를 사용하는게 낫다고 판단
 */
@RequiredArgsConstructor
public class OdsayFeignConfig {

    @Value("${odsay.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor odsayRequestInterceptor() {
        return requestTemplate -> {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            requestTemplate.query("apiKey", encodedKey);
            };
        }
}

