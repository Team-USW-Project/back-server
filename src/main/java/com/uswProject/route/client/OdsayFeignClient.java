package com.uswProject.route.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "odsayClient", url = "${odysay.api.base-url}", configuration = OdsayFeignConfig.class)
public interface OdsayFeignClient {

    @GetMapping("/searchPubTransPath")
    Object getSearchPubTransPath(
            @RequestParam("SX") double startX,
            @RequestParam("SY") double startY,
            @RequestParam("EX") double endX,
            @RequestParam("EY") double endY,
            @RequestParam(value = "OPT", required = false) Integer opt,
            @RequestParam(value = "SearchType", required = false) Integer searchType,
            @RequestParam(value = "SearchPathType", required = false) Integer searchPathType
    );
}
