package com.ssafy.wanderspot_backend.notification.controller;

import com.ssafy.wanderspot_backend.entity.Notification;
import com.ssafy.wanderspot_backend.notification.dto.NotificationDto;
import com.ssafy.wanderspot_backend.notification.service.NotificationService;
import com.ssafy.wanderspot_backend.notification.service.NotificationServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @MessageMapping("/sendNotification")
    public void sendNotification(String userId) {
        // 특정 사용자에게 메시지 전송
        List<Notification> allNotifications = notificationService.getAllNotifications(userId);
        List<NotificationDto> allNotificationDTOs = NotificationServiceImpl.convertToDTOList(allNotifications);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, allNotificationDTOs);
    }
}
