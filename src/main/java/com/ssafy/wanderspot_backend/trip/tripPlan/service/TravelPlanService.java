package com.ssafy.wanderspot_backend.trip.tripPlan.service;

import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TravelPlanService {

    TravelPlanDto saveTravelPlan(TravelPlanDto request);

    List<TravelPlanDto> getJoinedTravelPlans(String userId);

    List<TravelPlanDto> getCreatedTravelPlans(String userId);

    TravelPlanDto getTravelPlan(Long travelPlanId);

    TravelPlanDto updateTravelPlan(Long id, TravelPlanDto request);

    void deleteTravelPlan(Long id, String userId);

    Page<TravelPlanReviewDto> getTravelPlans(int page, int size);

}
