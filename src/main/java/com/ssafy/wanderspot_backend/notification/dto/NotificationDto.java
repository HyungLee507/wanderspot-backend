package com.ssafy.wanderspot_backend.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 정보를 나타내는 DTO")
public class NotificationDto {

    @Schema(description = "알림 ID", example = "1", required = true)
    private Long id;

    @Schema(description = "알림 메시지", example = "새로운 알림이 도착했습니다!", required = true)
    private String message;

    @Schema(description = "알림 읽음 여부", example = "false", required = true)
    private boolean isRead;

    @Schema(description = "알림 생성 시간 (ISO 형식)", example = "2024-11-25T10:00:00")
    private LocalDateTime createdAt;
}