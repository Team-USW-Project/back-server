package com.uswProject.route.dto.odsay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsayResult {
    private List<OdsayPath> path;
}
