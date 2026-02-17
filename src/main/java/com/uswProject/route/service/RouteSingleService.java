package com.uswProject.route.service;

import com.uswProject.route.entity.Place;
import com.uswProject.route.entity.RoutePlan;
import com.uswProject.route.entity.enums.RouteOption;
import com.uswProject.route.repository.TransitRouteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteSingleService {

    private final TransitRouteRepository transitRouteRepository;

    public List<RoutePlan> search(Place origin, Place destination, RouteOption option, int limit) {
        validate(origin, destination);

        RouteOption resolvedOption = resolvedOption(option);
        int resolvedLimit = resolvedLimit(limit);

        List<RoutePlan> routes = transitRouteRepository.search(origin, destination, option);
        if (routes == null || routes.isEmpty()) {
            return List.of();
        }

        return limitRoutes(routes, resolvedLimit);
    }

    private void validate(Place origin, Place destination) {
        if (origin == null) {
            throw new IllegalArgumentException("origin은 필수 값입니다.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("destination은 필수 값입니다.");
        }
    }

    private RouteOption resolvedOption(RouteOption option) {
        if (option == null) {
            return RouteOption.SHORTEST_TIME;
        }
        return option;
    }

    private int resolvedLimit(int limit) {
        if (limit < 1) {
            return 1;
        }
        return limit;
    }

    private List<RoutePlan> limitRoutes(List<RoutePlan> routes, int limit) {
        if (routes.size() <= limit) {
            return routes;
        }
        return routes.subList(0, limit);
    }
}
