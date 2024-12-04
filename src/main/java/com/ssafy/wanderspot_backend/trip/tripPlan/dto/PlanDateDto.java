package com.ssafy.wanderspot_backend.trip.tripPlan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "여행 날짜 DTO")
public class PlanDateDto {
    private LocalDateTime start;
    private LocalDateTime end;
}
