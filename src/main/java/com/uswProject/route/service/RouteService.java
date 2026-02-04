package com.uswProject.route.service;

import com.uswProject.route.client.ODsayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final ODsayClient odsayClient;

    @Value("${odsay.api.key}")
    private String apiKey;

    public RouteService(ODsayClient odsayClient) {
        this.odsayClient = odsayClient;
    }

    public Object getRoute(String sx, String sy, String ex, String ey) {
        return odsayClient.getSearchPubTransPath(sx, sy, ex, ey, apiKey);
    }
}
