package com.uswproject.meeting.service;

import com.uswproject.kakao.client.KakaoRoutingClient;
import com.uswproject.kakao.exception.KakaoApiException;
import com.uswproject.meeting.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class TravelRouteService {
    private final KakaoRoutingClient client;
    public List<TravelRouteDto> routes(TravelRoutesRequestDto request) {
        // 최대 4개만 동시에 호출합니다. 결과 순서는 참가자 입력 순서와 같습니다.
        var executor = Executors.newFixedThreadPool(Math.min(4, request.participants().size()));
        try {
            List<CompletableFuture<TravelRouteDto>> futures = new ArrayList<>();
            for (int i = 0; i < request.participants().size(); i++) {
                final int index = i;
                var participant = request.participants().get(i);
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try { return client.route(index, participant, request.destination()); }
                    catch (KakaoApiException exception) {
                        return TravelRouteDto.failure(index, participant.transportMode(), exception.getCode(), exception.getMessage());
                    }
                }, executor));
            }
            return futures.stream().map(CompletableFuture::join).toList();
        } finally { executor.shutdown(); }
    }
}
