package com.ssafy.wanderspot_backend.trip.tripPlan.controller;

import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.service.TravelPlanService;
import com.ssafy.wanderspot_backend.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel-plan")
@Tag(name = "TravelPlan API", description = "여행 계획 관련 API")
@RequiredArgsConstructor
@Slf4j
public class TravelPlanController {

    private final TravelPlanService travelPlanService;
    private final JWTUtil jwtUtil;

    @PostMapping
    @Operation(summary = "여행 계획 생성", description = "새로운 여행 계획을 생성합니다.")
    public ResponseEntity<TravelPlanDto> createTravelPlan(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody TravelPlanDto request) {
        String userId = jwtUtil.getUserId(accessToken);
        request.setCreateUserId(userId);
        TravelPlanDto savedPlan = travelPlanService.saveTravelPlan(request);
        return ResponseEntity.ok(savedPlan);
    }

    @GetMapping("/search")
    @Operation(summary = "여행 계획 조회", description = "ID를 통해 여행 계획을 조회합니다.")
    public ResponseEntity<TravelPlanDto> getTravelPlan(@RequestParam("id") Long id) {
        log.info("Received ID: {} ", id);
        TravelPlanDto response = travelPlanService.getTravelPlan(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created")
    @Operation(summary = "내가 만든 여행 계획 조회", description = "사용자가 만든 여행 계획을 조회합니다.")
    public ResponseEntity<List<TravelPlanDto>> getCreatedTravelPlans(
            @RequestHeader("Authorization") String accessToken) {
        String userId = jwtUtil.getUserId(accessToken);
        List<TravelPlanDto> travelPlans = travelPlanService.getCreatedTravelPlans(userId);
        return ResponseEntity.ok(travelPlans);
    }

    @GetMapping("/joined")
    @Operation(summary = "내가 참여 중인 여행 계획 조회", description = "사용자가 참여 중인 여행 계획을 조회합니다.")
    public ResponseEntity<List<TravelPlanDto>> getJoinedTravelPlans(
            @RequestHeader("Authorization") String accessToken) {
        String userId = jwtUtil.getUserId(accessToken);
        List<TravelPlanDto> travelPlans = travelPlanService.getJoinedTravelPlans(userId);
        return ResponseEntity.ok(travelPlans);
    }

    @PutMapping("/{id}")
    @Operation(summary = "여행 계획 수정", description = "기존 여행 계획을 수정합니다.")
    public ResponseEntity<TravelPlanDto> updateTravelPlan(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") Long id,
            @RequestBody TravelPlanDto request) {
        String userId = jwtUtil.getUserId(accessToken);
        request.setCreateUserId(userId);
        TravelPlanDto updatedPlan = travelPlanService.updateTravelPlan(id, request);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "여행 계획 삭제", description = "ID를 통해 여행 계획을 삭제합니다.")
    public ResponseEntity<Void> deleteTravelPlan(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") Long id) {
        String userId = jwtUtil.getUserId(accessToken);
        travelPlanService.deleteTravelPlan(id, userId);
        return ResponseEntity.noContent().build();
    }
}
