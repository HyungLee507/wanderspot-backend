package com.ssafy.wanderspot_backend.dart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "위도, 경도, 주소 정보를 포함하는 데이터 전송 객체")
public class LocationDto {

    @Schema(description = "경도 (Longitude)", example = "129.16")
    private String lng;

    @Schema(description = "위도 (Latitude)", example = "35.15")
    private String lat;

    @Schema(description = "주소 (Address)", example = "부산광역시 해운대구")
    private String addr;
}