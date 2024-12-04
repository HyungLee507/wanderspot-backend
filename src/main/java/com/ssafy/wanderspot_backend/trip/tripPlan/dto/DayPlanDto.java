package com.ssafy.wanderspot_backend.trip.tripPlan.dto;

import java.util.List;
import lombok.Data;

@Data
public class DayPlanDto {
    private List<TravelSpotDto> placeList;
}
