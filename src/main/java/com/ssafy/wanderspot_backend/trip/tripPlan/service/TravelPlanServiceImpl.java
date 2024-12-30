package com.ssafy.wanderspot_backend.trip.tripPlan.service;

import com.ssafy.wanderspot_backend.entity.DayPlan;
import com.ssafy.wanderspot_backend.entity.Member;
import com.ssafy.wanderspot_backend.entity.PlanDate;
import com.ssafy.wanderspot_backend.entity.TravelPlan;
import com.ssafy.wanderspot_backend.entity.TravelSpot;
import com.ssafy.wanderspot_backend.member.repository.MemberRepository;
import com.ssafy.wanderspot_backend.notification.service.NotificationService;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.DayPlanDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.PlanDateDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelSpotDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.repository.TravelPlanRepository;
import com.ssafy.wanderspot_backend.trip.tripspot.repository.TravelSpotRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelPlanServiceImpl implements TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final MemberRepository memberRepository;
    private final TravelSpotRepository travelSpotRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;


    @Transactional
    @Override
    public TravelPlanDto saveTravelPlan(TravelPlanDto request) {
        // 작성자 조회
        Member createUser = memberRepository.findById(request.getCreateUserId())
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다: ID = " + request.getCreateUserId()));

        // 참여 멤버 조회
        List<Member> joinMembers = memberRepository.findAllByUserIdIn(request.getJoinMemberIds());

        // PlanDate 생성
        PlanDate planDate = new PlanDate();
        planDate.setStart(request.getPlanDate().getStart());
        planDate.setEnd(request.getPlanDate().getEnd());

        // TravelPlan 생성
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setTitle(request.getTitle());
        travelPlan.setLocation(request.getLocation());
        travelPlan.setContent(request.getContent());
        travelPlan.setPlanDate(planDate);
        travelPlan.setCreateUser(createUser);
        travelPlan.setJoinMembers(joinMembers);

        // DayPlanList 생성
        List<DayPlan> dayPlans = mapDayPlanDtoToEntity(request.getDayPlanList(), travelPlan);
        travelPlan.setDayPlanList(dayPlans);

        // DayHouseList 생성
        List<DayPlan> dayHouses = mapDayPlanDtoToEntity(request.getDayHouseList(), travelPlan);
        travelPlan.setDayHouseList(dayHouses);

        // 초대된 사람들에게 알림 전송
        notifyParticipants(travelPlan.getJoinMembers(), travelPlan.getCreateUser().getUserId(),
                "친구 " + createUser.getUserId() + "님의 여행 계획에 초대하셨습니다.", false);

        travelPlanRepository.save(travelPlan);

        return mapEntityToTravelPlanDto(travelPlan);
    }

    private List<DayPlan> mapDayPlanDtoToEntity(List<DayPlanDto> dayPlanDtoList, TravelPlan travelPlan) {
        return dayPlanDtoList.stream().map(dayPlanDto -> {
            DayPlan dayPlan = new DayPlan();
//            dayPlan.setTravelPlan(travelPlan); // TravelPlan 연관 설정

            // TravelSpot 리스트 생성
            List<TravelSpot> travelSpots = dayPlanDto.getPlaceList().stream().map(travelSpotDto -> {
                TravelSpot travelSpot = new TravelSpot();
                travelSpot.setKakaoMapId(travelSpotDto.getKakaoMapId());
                travelSpot.setAddressName(travelSpotDto.getAddressName());
                travelSpot.setPlaceName(travelSpotDto.getPlaceName());
                travelSpot.setCategoryName(travelSpotDto.getCategoryName());
                travelSpot.setLatitude(travelSpotDto.getLat());
                travelSpot.setLongitude(travelSpotDto.getLng());
                travelSpot.setCity(travelSpotDto.getCity());
                travelSpot.setDayPlan(dayPlan); // DayPlan 연관 설정
                return travelSpot;
            }).collect(Collectors.toList());

            dayPlan.setPlaceList(travelSpots); // TravelSpot 리스트를 DayPlan에 설정
            return dayPlan;
        }).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<TravelPlanDto> getCreatedTravelPlans(String userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return member.getCreatedPlans().stream()
                .map(this::mapEntityToTravelPlanDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TravelPlanDto> getJoinedTravelPlans(String userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return member.getJoinedPlans().stream()
                .map(this::mapEntityToTravelPlanDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public TravelPlanDto getTravelPlan(Long travelPlanId) {
        log.info("travelPlan ID is = {} ", travelPlanId);
        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행 계획을 찾을 수 없습니다."));
        log.info("travel plan data is ={}", travelPlan);
        log.info("travel DayPlan data is ={}", travelPlan.getDayPlanList());
        log.info("travel DayHouse data is ={}", travelPlan.getDayHouseList());
        for (DayPlan dayPlan : travelPlan.getDayPlanList()) {
            log.info("dayPlan is {}", dayPlan);
        }
        for (DayPlan dayPlan : travelPlan.getDayHouseList()) {
            log.info("dayPlan is {}", dayPlan);
        }
        log.info("travel plan data is ={}", travelPlan);

        return mapEntityToTravelPlanDto(travelPlan);
    }

    @Transactional
    @Override
    public TravelPlanDto updateTravelPlan(Long id, TravelPlanDto request) {
        TravelPlan travelPlan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행 계획을 찾을 수 없습니다."));

        if (!travelPlan.getCreateUser().getUserId().equals(request.getCreateUserId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 기본 필드 업데이트
        travelPlan.setTitle(request.getTitle());
        travelPlan.setLocation(request.getLocation());
        travelPlan.setContent(request.getContent());

        // PlanDate 업데이트
        PlanDate planDate = travelPlan.getPlanDate();
        planDate.setStart(request.getPlanDate().getStart());
        planDate.setEnd(request.getPlanDate().getEnd());
        travelPlan.setPlanDate(planDate);

        // 참여 멤버 업데이트
        List<Member> joinMembers = memberRepository.findAllByUserIdIn(request.getJoinMemberIds());
        travelPlan.setJoinMembers(joinMembers);

        // DayPlanList 업데이트
        travelPlan.getDayPlanList().clear();
        List<DayPlan> updatedDayPlans = mapDayPlanDtoToEntity(request.getDayPlanList(), travelPlan);
        travelPlan.getDayPlanList().addAll(updatedDayPlans);

        // DayHouseList 업데이트
        travelPlan.getDayHouseList().clear();
        List<DayPlan> updatedDayHouses = mapDayPlanDtoToEntity(request.getDayHouseList(), travelPlan);
        travelPlan.getDayHouseList().addAll(updatedDayHouses);

        List<Member> participants = travelPlan.getJoinMembers();
        participants.add(travelPlan.getCreateUser()); // 생성자 포함
        String updateUserId = request.getUpdateUserId();
        log.info("updateUser id is ={}", updateUserId);
        // 알림 전송 (수정자를 제외하고 알림 전송)
        notifyParticipants(participants, updateUserId,
                "친구 " + updateUserId + "님이 " + travelPlan.getTitle() + "여행 계획을 수정하셨습니다.", true);
        // 저장
        travelPlanRepository.save(travelPlan);

        return mapEntityToTravelPlanDto(travelPlan);
    }


    @Transactional
    @Override
    public void deleteTravelPlan(Long id, String userId) {
        TravelPlan travelPlan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행 계획을 찾을 수 없습니다."));

        if (!travelPlan.getCreateUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        travelPlanRepository.delete(travelPlan);
    }

    public void notifyParticipants(List<Member> participants, String creatorId, String messageTemplate,
                                   boolean excludeCreator) {
        log.info("notifyParticipants 진입");
        for (Member participant : participants) {
            // 생성자 제외 조건
            if (excludeCreator && participant.getUserId().equals(creatorId)) {
                continue;
            }
            // 알림 메시지 생성 및 전송
            notificationService.createNotification(participant.getUserId(), messageTemplate);
//            messagingTemplate.convertAndSend("/topic/notifications/" + participant.getUserId(), messageTemplate);
        }
    }

    // 이건 문제가 없는거 같은데
    private TravelPlanDto mapEntityToTravelPlanDto(TravelPlan travelPlan) {
        TravelPlanDto dto = new TravelPlanDto();
        dto.setId(travelPlan.getId());
        dto.setTitle(travelPlan.getTitle());
        dto.setLocation(travelPlan.getLocation());
        dto.setContent(travelPlan.getContent());

        PlanDateDto planDateDto = new PlanDateDto();
        planDateDto.setStart(travelPlan.getPlanDate().getStart());
        planDateDto.setEnd(travelPlan.getPlanDate().getEnd());
        dto.setPlanDate(planDateDto);

        dto.setCreateUserId(travelPlan.getCreateUser().getUserId());
        dto.setJoinMemberIds(travelPlan.getJoinMembers().stream()
                .map(Member::getUserId).collect(Collectors.toList()));

        dto.setDayPlanList(mapDayPlanToDto(travelPlan.getDayPlanList()));
        dto.setDayHouseList(mapDayPlanToDto(travelPlan.getDayHouseList()));

        return dto;
    }

    private List<DayPlanDto> mapDayPlanToDto(List<DayPlan> dayPlans) {
        return dayPlans.stream().map(dayPlan -> {
            DayPlanDto dto = new DayPlanDto();
            dto.setPlaceList(dayPlan.getPlaceList().stream().map(travelSpot -> {
                TravelSpotDto spotDto = new TravelSpotDto();
                spotDto.setId(travelSpot.getId());
                spotDto.setKakaoMapId(travelSpot.getKakaoMapId());
                spotDto.setAddressName(travelSpot.getAddressName());
                spotDto.setPlaceName(travelSpot.getPlaceName());
                spotDto.setCategoryName(travelSpot.getCategoryName());
                spotDto.setLat(travelSpot.getLatitude());
                spotDto.setLng(travelSpot.getLongitude());
                spotDto.setCity(travelSpot.getCity());
                return spotDto;
            }).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 페이징 처리된 여행 계획 목록을 가져옵니다.
     *
     * @param page 현재 페이지 번호 (0부터 시작)
     * @param size 한 페이지에 표시할 항목 수
     * @return 페이징 처리된 TravelPlanReviewDto 페이지
     */
    @Override
    public Page<TravelPlanReviewDto> getTravelPlans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return travelPlanRepository.findAllAsDto(pageable);
    }
}
