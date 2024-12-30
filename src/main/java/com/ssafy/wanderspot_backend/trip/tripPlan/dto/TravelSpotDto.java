package com.ssafy.wanderspot_backend.trip.tripPlan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(description = "여행지 정보 DTO")
public class TravelSpotDto {
    @Schema(description = "여행지 ID", example = "1001")
    private Long id;

    @Schema(description = "카카오 지도 ID", example = "KakaoID12345")
    private String kakaoMapId;

    @Schema(description = "주소", example = "부산 해운대구")
    private String addressName;

    @Schema(description = "장소 이름", example = "해운대 해수욕장")
    private String placeName;

    @Schema(description = "카테고리 이름", example = "관광명소")
    private String categoryName;

    @Schema(description = "위도", example = "35.158523")
    private double lat;

    @Schema(description = "경도", example = "129.159854")
    private double lng;

    @Schema(description = "도시 이름", example = "부산")
    private String city;
}
