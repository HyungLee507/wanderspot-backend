package com.ssafy.wanderspot_backend.trip.tripPlan.repository;

import com.ssafy.wanderspot_backend.entity.TravelPlan;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {


    @Query("SELECT new com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto(" +
            "t.id, t.title, t.location, t.content, t.planDate, t.createUser.userId) " +
            "FROM TravelPlan t")
    Page<TravelPlanReviewDto> findAllAsDto(Pageable pageable);

    @Query("SELECT new com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto(tp.id, tp.title, tp.location, tp.content,tp.planDate, tp.createUser.userId) " +
            "FROM TravelPlan tp WHERE tp.createUser.userId = :userId")
    List<TravelPlanReviewDto> findTravelPlansByUserId(@Param("userId") String userId);

    @Query("SELECT tp FROM TravelPlan tp " +
            "JOIN FETCH tp.createUser " +
            "JOIN FETCH tp.joinMembers " +
            "WHERE tp.id = :travelPlanId")
    Optional<TravelPlan> findByIdWithDetails(@Param("travelPlanId") Long travelPlanId);

    // 작성자가 만든 여행 계획 조회
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.createUser.userId = :userId")
    List<TravelPlan> findByCreateUserId(@Param("userId") String userId);

    // 참여자로 포함된 여행 계획 조회
    @Query("SELECT tp FROM TravelPlan tp JOIN tp.joinMembers jm WHERE jm.userId = :userId")
    List<TravelPlan> findByJoinMemberId(@Param("userId") String userId);


}
