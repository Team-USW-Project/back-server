package com.uswProject.userLastTime.service;

import com.uswProject.userLastTime.client.SearchRouteOpenFeign;
import com.uswProject.userLastTime.dto.*;
import com.uswProject.userLastTime.exception.LastTimeErrorCode;
import com.uswProject.userLastTime.exception.LastTimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLastTimeService {

    private final SearchRouteOpenFeign client;

    public List<OdsayResponse.Path> routeList(double startX, double startY, double endX, double endY){

        OdsayResponse response = client.searchPath(startX, startY, endX, endY);

        if (response == null || response.getResult() == null || response.getResult().getPath() == null) {
            throw new LastTimeException(LastTimeErrorCode.ROUTE_NOT_FOUND);
        }

        return response.getResult().getPath().stream()
                .limit(10)
                .toList();
    }

    // 막차 시간 구하는 메서드
    public String LastTime(LastTimeRequest dto) {

        // 세부경로
        List<SummariesPathRequest> paths = dto.getSubPath();
        // 경로 List Size
        int count = paths.size();

        // lastTime 즉 출발지에 도착해야 하는 시간
        LocalTime lastTime = null;

        for (int i = count - 1; i >=0; i--) {
            SummariesPathRequest path = paths.get(i);

            if ( path.getTrafficType() == 1 ) {
                SubwayResponse subwayResponse = subwayRoute(path.getStartID());

                // 이동시간
                int subWayTime = path.getSectionTime();

                LocalTime targetTime;

                if(lastTime == null) {
                    // 제일 늦은 막차 시간 구하는 로직 TODO: 리팩토링 예정
                    targetTime = LocalTime.of(23, 40);
                } else {
                    targetTime = lastTime.minusMinutes(subWayTime + 20);
                }

                // 도착시간.
                String safeTime;

                //상행
                if(path.getWayCode() == 1) {
                    safeTime = findTrainTime(subwayResponse, path.getWayCode(), targetTime.toString());
                }
                // 하행
                else if (path.getWayCode() == 2) {
                    safeTime = findTrainTime(subwayResponse, path.getWayCode(), targetTime.toString());
                } else throw new LastTimeException(LastTimeErrorCode.SUBWAY_ROUTE_NOT_FOUND);

                if(safeTime == null) {
                    throw new LastTimeException(LastTimeErrorCode.SUBWAY_ROUTE_NOT_FOUND);
                }

                lastTime = LocalTime.parse(safeTime);
            }
            /**
             * 세부 경로 조회 시 Bus일때 Bus노선 상세 조회를 통해 막차시간, idx를 통해 막차시간이 몇시인지 추정하고,
             * 10분 여유 시간을 빼 최종적으로 언제까지 정류장에 도착해야 하는지 시간 표시
             */
            else if (path.getTrafficType() == 2) {
                BusResponse busResponse = busRoute(path.getLane().get(0).getBusID());

                if (busResponse.getResult().getBusID() != path.getLane().get(0).getBusID()) {
                    throw new LastTimeException(LastTimeErrorCode.BUS_NOT_CORRECT);
                }

                // String형식을 LocalTime 으로 전환
                LocalTime busLastTime = LocalTime.parse(busResponse.getResult().getBusLastTime());

                int sectionTime = path.getSectionTime();

                int idx = findIdx(busResponse.getResult().getStation(), path.getStartID());


                // 종점부터 버스 정류장까지 몇 분이 걸리는지 계산
                int busStopTime = busTime(idx);

                // 정류장에 버스가 도착하는 시간
                LocalTime result = busLastTime.plusMinutes(busStopTime);

                if(lastTime == null) {
                    lastTime = result.minusMinutes(10);
                } else {
                    LocalTime result1 = lastTime.minusMinutes(sectionTime + 10);

                    if(result.isAfter(result1)) {
                        int interval = getBusInterval(busResponse.getResult().getBusInterval());

                        while(result.isAfter(result1)) {
                            result = result.minusMinutes(interval);
                        }
                    }
                    lastTime = result.minusMinutes(10);
                }

                lastTime = result.minusMinutes(10);

            } else if (path.getTrafficType() == 3) {
                int StreetTime = path.getSectionTime();
                // lastTime이 null이 아니라는 것은 마지막 집가는 순서라는 뜻
                if( lastTime != null) {
                    lastTime = lastTime.minusMinutes(StreetTime + 5);
                }
            }
        }

        return lastTime.toString();
    }

    // idx값을 통한 bus가 정류장에 실제로 도착하는 시간 구하는 메서드
    public int busTime(int subCount) {
        return (int) Math.round(subCount * 1.5);
    }

    // 버스노선 상세 조회 (막차 시간 구하기)
    public BusResponse busRoute(int busID) {
        BusResponse busResponse = client.searchBus(busID);
        return busResponse;
    }
    // 지하철 전체시간표 조회 (시간표 조회를 통한 막차시간 구하기)
    public SubwayResponse subwayRoute(int stationID) {
        SubwayResponse subwayResponse = client.searchSubway(stationID);
        return subwayResponse;
    }

    // 지하철을 타야하는 시간 구하는 메서드
    public String findTrainTime(SubwayResponse subwayResponse, int wayCode, String lastTime) {

        if(subwayResponse == null || subwayResponse.getResult() == null || subwayResponse.getResult().getWeekdaySchedule() == null) {
            throw new LastTimeException(LastTimeErrorCode.SUBWAY_ROUTE_NOT_FOUND);
        }

        SubwayResponse.WeekdaySchedule schedule = subwayResponse.getResult().getWeekdaySchedule();

        String safeTime = null;

        if(wayCode == 1) {
            List<SubwayResponse.up> upList = schedule.getUp();
            if(upList == null || upList.isEmpty()) {
                throw new LastTimeException(LastTimeErrorCode.SUBWAY_ROUTE_NOT_FOUND);
            }

            for(SubwayResponse.up up : upList) {
                String departureTime = up.getDepartureTime();

                if(departureTime.compareTo(lastTime) < 0) {
                    safeTime = departureTime;
                } else {
                    break;
                }
            }
        } else {
            List<SubwayResponse.down> downList = schedule.getDown();
            if(downList == null || downList.isEmpty()) {
                throw new LastTimeException(LastTimeErrorCode.SUBWAY_ROUTE_NOT_FOUND);
            }

            for(SubwayResponse.down down : downList) {
                String departureTime = down.getDepartureTime();
                if(departureTime.compareTo(lastTime) < 0) {
                    safeTime = departureTime;
                }  else {
                    break;
                }
            }
        }
        return safeTime;
    }

    // 버스 배차 시간 구하는 메서드
    private int getBusInterval(String busInterval) {
        if(busInterval == null || busInterval.isBlank()) {
            return 15;
        }
        try {
            return Integer.parseInt(busInterval.trim());
        } catch (NumberFormatException e) {
            log.error("캐스팅 오류");
            return 15;
        }
    }

    // 타야 하는 버스 정류장이 막차 출발지로부터 몇번째에 존재하는지 구하는 메서드
    private Integer findIdx(List<BusResponse.station> stations, int targetStationId) {
        for(BusResponse.station station : stations) {
            if(station.getStationID() == targetStationId) {
                return station.getIdx();
            }
        }
        throw new LastTimeException(LastTimeErrorCode.BUS_STATION_NOT_FOUND);
    }
}
