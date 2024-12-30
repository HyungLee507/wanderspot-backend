package com.ssafy.wanderspot_backend.notification.service;

import com.ssafy.wanderspot_backend.entity.Notification;
import java.util.List;

public interface NotificationService {

    Notification createNotification(String userId, String message);

    List<Notification> getAllNotifications(String userId);

    List<Notification> getUnreadNotifications(String userId);

    void markNotificationAsRead(Long notificationId);

}
