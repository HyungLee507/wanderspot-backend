package com.ssafy.wanderspot_backend.trip.tripPlan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "여행 계획 DTO")
public class TravelPlanDto {
    @Schema(description = "여행 계획 ID", example = "1")
    private Long id;

    @Schema(description = "여행 이름", example = "부산 여행")
    private String title;

    @Schema(description = "여행 위치", example = "부산")
    private String location;

    @Schema(description = "여행 설명", example = "바닷가 여행")
    private String content;

    @Schema(description = "여행 기간")
    private PlanDateDto planDate;

    @Schema(description = "작성자 ID", example = "user123")
    private String createUserId;

    @Schema(description = "수정자 ID", example = "user456")
    private String updateUserId;

    @Schema(description = "참여자 목록", example = "[\"user456\", \"user789\"]")
    private List<String> joinMemberIds;

    @Schema(description = "일정 리스트")
    private List<DayPlanDto> dayPlanList;

    @Schema(description = "숙소 리스트")
    private List<DayPlanDto> dayHouseList;


}