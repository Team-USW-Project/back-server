package com.uswProject.route.repository;

import com.uswProject.route.entity.Place;
import com.uswProject.route.entity.RoutePlan;
import com.uswProject.route.entity.enums.RouteOption;
import java.util.List;

public interface TransitRouteRepository {
    List<RoutePlan> search(Place origin, Place destination, RouteOption option);
}
