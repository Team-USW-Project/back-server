package com.uswproject.meeting.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class MapConfigController {

    private final String javascriptKey;

    public MapConfigController(@Value("${kakao.javascript-key:}") String javascriptKey) {
        this.javascriptKey = javascriptKey.trim();
    }

    // 지도용 JavaScript 키는 브라우저에서 사용합니다. REST API 키는 반환하지 않습니다.
    @GetMapping("/config")
    public ResponseEntity<MapConfigDto> getConfig() {
        if (javascriptKey.isBlank()) {
            return ResponseEntity.status(503).body(new MapConfigDto(""));
        }
        return ResponseEntity.ok().cacheControl(org.springframework.http.CacheControl.noStore())
                .body(new MapConfigDto(javascriptKey));
    }

    public record MapConfigDto(String javascriptKey) {
    }
}
