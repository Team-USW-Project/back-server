package com.uswProject.route.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "odsayClient", url = "${odysay.api.base-url}")
public interface RouteClient {

    @GetMapping("/searchPubTransPath")
    Object getSearchPubTransPath(
            @RequestParam("SX") String startX,
            @RequestParam("SY") String startY,
            @RequestParam("EX") String endX,
            @RequestParam("EY") String endY,
            @RequestParam("apiKey") String apiKey // 발급받은 ODsay API Key
    );
}
