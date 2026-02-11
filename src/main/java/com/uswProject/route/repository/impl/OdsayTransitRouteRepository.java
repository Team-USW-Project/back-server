package com.uswProject.route.repository.impl;

import com.uswProject.route.client.OdsayFeignClient;
import com.uswProject.route.entity.Place;
import com.uswProject.route.entity.RoutePlan;
import com.uswProject.route.entity.enums.RouteOption;
import com.uswProject.route.repository.TransitRouteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OdsayTransitRouteRepository implements TransitRouteRepository {

    private final OdsayFeignClient client;

    @Override
    public List<RoutePlan> search(Place origin, Place destination, RouteOption option) {
        return List.of();
    }
}
