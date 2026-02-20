package com.uswProject.route.repository.impl;

import com.uswProject.route.client.OdsayFeignClient;
import com.uswProject.route.dto.odsay.OdsaySearchResponse;
import com.uswProject.route.entity.Place;
import com.uswProject.route.entity.RoutePlan;
import com.uswProject.route.entity.enums.RouteOption;
import com.uswProject.route.mapper.OdsayRouteMapper;
import com.uswProject.route.repository.TransitRouteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OdsayTransitRouteRepository implements TransitRouteRepository {

    private final OdsayFeignClient client;
    private final OdsayRouteMapper mapper;

    @Value("${odsay.opt:0}")
    private int defaultOpt;

    @Override
    public List<RoutePlan> search(Place origin, Place destination, RouteOption option) {
        int opt = defaultOpt;
        if (option == RouteOption.MIN_TRANSFER) {
            opt = 1;
        }

        OdsaySearchResponse res = client.getSearchPubTransPathT(
                origin.getLng(), origin.getLat(),
                destination.getLng(), destination.getLat(),
                opt,
                0,
                0
        );

        if (res != null && res.getError() != null) {
            throw new IllegalStateException("Odsay error: " + res.getError().getCode() + " " + res.getError().getMessage());
        }

        return mapper.toRoutePlans(res, origin, destination);
    }
}
