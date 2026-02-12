package com.uswProject.userLastTime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LastTimeRequest {
    private List<SummariesPathRequest> subPath;
}
