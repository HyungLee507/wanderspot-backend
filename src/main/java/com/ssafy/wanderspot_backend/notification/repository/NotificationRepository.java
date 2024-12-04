package com.ssafy.wanderspot_backend.notification.repository;

import com.ssafy.wanderspot_backend.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자 모든 알림 조회 (연관관계 활용)
    List<Notification> findByUser_UserId(String userId);

    // 특정 사용자 읽지 않은 알림만 조회
    List<Notification> findByUser_UserIdAndReadFalse(String userId);

    // 특정 사용자 읽은 알림만 조회
    List<Notification> findByUser_UserIdAndReadTrue(String userId);

    // 특정 사용자 알림, 최신 순으로 조회
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(String userId);

    // 특정 사용자 특정 기간 알림 조회
    List<Notification> findByUser_UserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);
}
