package com.ssafy.wanderspot_backend.trip.tripPlan.dto;

import com.ssafy.wanderspot_backend.entity.PlanDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TravelPlanReviewDto {

    @Schema(description = "여행 계획 ID", example = "1")
    private Long id;

    @Schema(description = "여행 계획 제목", example = "제주도 여행")
    private String title;

    @Schema(description = "여행 위치", example = "제주도")
    private String location;

    @Schema(description = "여행 설명", example = "제주도에서의 멋진 여행을 계획")
    private String content;

    @Schema(description = "여행 기간")
    private PlanDate planDate;

    @Schema(description = "작성자 ID", example = "user123")
    private String createUserId;

}
