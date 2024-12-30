package com.ssafy.wanderspot_backend.trip.tripspot.repository;

import com.ssafy.wanderspot_backend.entity.TravelSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelSpotRepository extends JpaRepository<TravelSpot, Long> {
}
