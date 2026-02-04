package com.uswProject.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 외부 API를 사용하기 위해 Open Feign HTTP 통신 기술 사용
 * 하지만 Application파일에 @EnableFeignClients를 달게되면 Test 수행 시에도 Feign Client를 스캔하여 테스트가 무거워진다.
 * 또한 불필요한 의존성 주입 에러가 발생하는 것을 막기 위해 따로 FeignConfig로 분리하였음
 * 여기서는 인코더, 디코더등 여러가지를 커스텀하여 작성할 수 있다.
 */
@Configuration
@EnableFeignClients("com.uswProject") //FeignClient가 위치한 패키지
public class OpenFeignConfig {

}
