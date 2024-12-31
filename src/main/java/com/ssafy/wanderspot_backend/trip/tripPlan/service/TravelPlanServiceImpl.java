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
        List<DayPlan> dayPlans = mapDayPlanDtoToEntity(request.getDayPlanList());
        travelPlan.setDayPlanList(dayPlans);

        // DayHouseList 생성
        List<DayPlan> dayHouses = mapDayPlanDtoToEntity(request.getDayHouseList());
        travelPlan.setDayHouseList(dayHouses);

        // 여행 계획 저장
        travelPlanRepository.save(travelPlan);

        // 초대된 사람들에게 알림 전송
        notifyParticipants(travelPlan.getJoinMembers(), travelPlan.getCreateUser().getUserId(),
                "친구 " + createUser.getUserId() + "님의 여행 계획에 초대하셨습니다.", false);

        return mapEntityToTravelPlanDto(travelPlan);
    }

    @Transactional(readOnly = true)
    public List<TravelPlanDto> getCreatedTravelPlans(String userId) {
        return travelPlanRepository.findByCreateUserId(userId).stream()
                .map(this::mapEntityToTravelPlanDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TravelPlanDto> getJoinedTravelPlans(String userId) {
        return travelPlanRepository.findByJoinMemberId(userId).stream()
                .map(this::mapEntityToTravelPlanDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TravelPlanDto getTravelPlan(Long travelPlanId) {
        TravelPlan travelPlan = travelPlanRepository.findByIdWithDetails(travelPlanId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행 계획을 찾을 수 없습니다."));

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

        // 참여 멤버 업데이트
        List<Member> joinMembers = memberRepository.findAllByUserIdIn(request.getJoinMemberIds());
        travelPlan.setJoinMembers(joinMembers);

        // DayPlanList 업데이트
        List<DayPlan> updatedDayPlans = mapDayPlanDtoToEntity(request.getDayPlanList());
        travelPlan.setDayPlanList(updatedDayPlans);

        // DayHouseList 업데이트
        List<DayPlan> updatedDayHouses = mapDayPlanDtoToEntity(request.getDayHouseList());
        travelPlan.setDayHouseList(updatedDayHouses);

        travelPlanRepository.save(travelPlan);

        notifyParticipants(travelPlan.getJoinMembers(), request.getUpdateUserId(),
                "친구 " + request.getUpdateUserId() + "님이 " + travelPlan.getTitle() + " 여행 계획을 수정하셨습니다.", true);

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

    private void notifyParticipants(List<Member> participants, String creatorId, String messageTemplate, boolean excludeCreator) {
        for (Member participant : participants) {
            if (excludeCreator && participant.getUserId().equals(creatorId)) {
                continue;
            }
            notificationService.createNotification(participant.getUserId(), messageTemplate);
        }
    }

    private List<DayPlan> mapDayPlanDtoToEntity(List<DayPlanDto> dayPlanDtoList) {
        return dayPlanDtoList.stream().map(dayPlanDto -> {
            DayPlan dayPlan = new DayPlan();
            List<TravelSpot> travelSpots = dayPlanDto.getPlaceList().stream().map(travelSpotDto -> {
                TravelSpot travelSpot = new TravelSpot();
                travelSpot.setKakaoMapId(travelSpotDto.getKakaoMapId());
                travelSpot.setAddressName(travelSpotDto.getAddressName());
                travelSpot.setPlaceName(travelSpotDto.getPlaceName());
                travelSpot.setCategoryName(travelSpotDto.getCategoryName());
                travelSpot.setLatitude(travelSpotDto.getLat());
                travelSpot.setLongitude(travelSpotDto.getLng());
                travelSpot.setCity(travelSpotDto.getCity());
                travelSpot.setDayPlan(dayPlan);
                return travelSpot;
            }).collect(Collectors.toList());
            dayPlan.setPlaceList(travelSpots);
            return dayPlan;
        }).collect(Collectors.toList());
    }

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

    @Transactional(readOnly = true)
    @Override
    public Page<TravelPlanReviewDto> getTravelPlans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return travelPlanRepository.findAllAsDto(pageable);
    }
}
