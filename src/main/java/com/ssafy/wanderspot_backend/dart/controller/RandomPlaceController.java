package com.ssafy.wanderspot_backend.dart.controller;

import com.ssafy.wanderspot_backend.dart.dto.LocationDto;
import com.ssafy.wanderspot_backend.dart.service.RandomPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/random")
@Tag(name = "랜덤 장소 추천 컨트롤러", description = "주변 관광지의 위도와 경도를 랜덤하게 반환하는 API")
@RequiredArgsConstructor
public class RandomPlaceController {
    private final RandomPlaceService randomPlaceService;

    @GetMapping("/candidate")
    @Operation(summary = "랜덤 좌표 추출",
            description = "입력받은 위도(lat)와 경도(lng)를 기준으로 주변 관광지의 위도와 경도를 랜덤하게 3개 반환합니다.")
    public List<LocationDto> getRandomCoordinates(
            @Parameter(description = "기준 위치의 경도 (예: 129.16)", required = true, example = "129.16")
            @RequestParam(value = "lng", required = true) String lng,

            @Parameter(description = "기준 위치의 위도 (예: 35.15)", required = true, example = "35.15")
            @RequestParam(value = "lat", required = true) String lat) {
        return randomPlaceService.getRandomCoordinates(lng, lat);
    }
}
