package com.ssafy.wanderspot_backend.notification.service;

import com.ssafy.wanderspot_backend.entity.Member;
import com.ssafy.wanderspot_backend.entity.Notification;
import com.ssafy.wanderspot_backend.member.repository.MemberRepository;
import com.ssafy.wanderspot_backend.notification.dto.NotificationDto;
import com.ssafy.wanderspot_backend.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;

    /**
     * 알림 생성 및 저장
     */
    @Override
    @Transactional
    public Notification createNotification(String userId, String message) {
        // 사용자 객체 조회
        log.info("Notification 생성 진입");
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 알림 생성
        Notification notification = new Notification();
        notification.setUser(user);
//        StringBuilder sb = new StringBuilder();
//        sb.append(userId).append(message);
        notification.setMessage(message);

        notification.setRead(false); // 초기 상태는 '읽지 않음'
        notification.setCreatedAt(LocalDateTime.now());

        // DB에 저장
        notificationRepository.save(notification);
        List<Notification> allNotifications = getAllNotifications(userId);
        List<NotificationDto> allNotificationDTOs = convertToDTOList(allNotifications);
        log.info("데이터 확인 {}", allNotificationDTOs);
        log.info("userId is {} ", userId);
        // WebSocket을 통해 실시간 전송
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, allNotificationDTOs);

        return notification;
    }


    /**
     * 사용자별 모든 알림 조회
     */
    @Override
    public List<Notification> getAllNotifications(String userId) {
        return notificationRepository.findByUser_UserId(userId); // 사용자 ID로 조회
    }

    /**
     * 읽지 않은 알림 조회
     */
    @Override
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUser_UserIdAndReadFalse(userId); // 읽지 않은 알림 조회
    }

    /**
     * 특정 알림 읽음 처리
     */
    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.setRead(true); // 읽음 상태 변경
        notificationRepository.save(notification);
    }

    public static List<NotificationDto> convertToDTOList(List<Notification> notifications) {
        return notifications.stream()
                .map(Notification::convertToDTO)
                .collect(Collectors.toList());
    }


}
