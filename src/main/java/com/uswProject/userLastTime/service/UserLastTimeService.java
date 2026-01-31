package com.uswProject.userLastTime.service;

import com.uswProject.userLastTime.client.SearchRouteOpenFeign;
import com.uswProject.userLastTime.dto.OdsayResponse;
import com.uswProject.userLastTime.exception.LastTimeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLastTimeService {

    private final SearchRouteOpenFeign client;

    public List<OdsayResponse.Path> routeList(double startX, double startY, double endX, double endY){

        OdsayResponse response = client.searchPath(startX, startY, endX, endY);

        if (response == null || response.getResult() == null || response.getResult().getPath() == null) {
            System.out.println(LastTimeErrorCode.ROUTE_NOT_FOUND);
            return Collections.emptyList();
        }

        return response.getResult().getPath().stream()
                .limit(10)
                .toList();
    }
}
