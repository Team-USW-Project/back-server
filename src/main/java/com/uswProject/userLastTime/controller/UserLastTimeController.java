package com.uswProject.userLastTime.controller;

import com.uswProject.global.success.SuccessResponse;
import com.uswProject.userLastTime.dto.OdsayResponse;
import com.uswProject.userLastTime.exception.LastTimeSuccessCode;
import com.uswProject.userLastTime.service.UserLastTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/userLastTimes")
@RequiredArgsConstructor
public class UserLastTimeController {

    private final UserLastTimeService userLastTimeService;

    @GetMapping("/routes")
    public ResponseEntity<SuccessResponse> searchRoutes(
            @RequestParam double SX,
            @RequestParam double SY,
            @RequestParam double EX,
            @RequestParam double EY
    ) {

        List<OdsayResponse.Path> routes = userLastTimeService.routeList(SX, SY, EX, EY);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(
                        LastTimeSuccessCode.Last_TIME_SUCCESS_SEARCH_ROUTE,
                        routes
                ));
    }
}
