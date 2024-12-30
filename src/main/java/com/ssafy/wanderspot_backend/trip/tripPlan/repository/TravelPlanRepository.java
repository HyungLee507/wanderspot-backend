package com.ssafy.wanderspot_backend.trip.tripPlan.repository;

import com.ssafy.wanderspot_backend.entity.TravelPlan;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {


    @Query("SELECT new com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto(" +
            "t.id, t.title, t.location, t.content, t.planDate, t.createUser.userId) " +
            "FROM TravelPlan t")
    Page<TravelPlanReviewDto> findAllAsDto(Pageable pageable);
}
