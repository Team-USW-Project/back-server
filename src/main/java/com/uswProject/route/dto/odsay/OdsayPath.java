package com.uswProject.route.dto.odsay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsayPath {
    private int pathType;
    private OdsayInfo info;
    private List<OdsaySubPath> subPath;
}
