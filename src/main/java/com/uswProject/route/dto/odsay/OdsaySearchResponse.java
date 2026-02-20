package com.uswProject.route.dto.odsay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsaySearchResponse {
    private OdsayResult result;
    private OdsayError error;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OdsayError {
        private String message;
        private String code;
    }
}
