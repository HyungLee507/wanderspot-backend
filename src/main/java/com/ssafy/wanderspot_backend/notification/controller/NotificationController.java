package com.ssafy.wanderspot_backend.notification.controller;

import com.ssafy.wanderspot_backend.entity.Notification;
import com.ssafy.wanderspot_backend.notification.service.NotificationService;
import com.ssafy.wanderspot_backend.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "알림 컨트롤러", description = "알림 읽음 처리 및 관련 기능을 담당하는 컨트롤러")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JWTUtil jwtUtil;


    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@RequestHeader("Authorization") String accessToken,
                                                       @PathVariable("notification_id") @Parameter(description = "읽음 처리할 알림 ID", required = true) Long id) {
        String userId = jwtUtil.getUserId(accessToken);
        try {
            notificationService.markNotificationAsRead(id);
            // 사용자 전체 알림 목록 조회
            List<Notification> allNotifications = notificationService.getAllNotifications(userId);

            // WebSocket으로 알림 목록 전송
            messagingTemplate.convertAndSend("/topic/notifications/" + userId, allNotifications);

            log.info("알림 읽음 처리 및 WebSocket 전송 완료: NotificationId={}, UserId={}", id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("알림 읽음 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


}