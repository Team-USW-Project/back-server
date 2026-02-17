package com.uswProject.route.service;

import com.uswProject.route.client.OdsayFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final OdsayFeignClient odsayClient;

    public RouteService(OdsayFeignClient odsayClient) {
        this.odsayClient = odsayClient;
    }

    public Object getRoute(double sx, double sy, double ex, double ey, int opt, int searchType, int searchPathType) {
        return odsayClient.getSearchPubTransPath(sx, sy, ex, ey, opt, searchType, searchPathType);
    }
}
