package com.ssafy.wanderspot_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TravelSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kakaoMapId;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String city;

    @ManyToOne
    @JoinColumn(name = "day_plan_id")
    private DayPlan dayPlan; // 단일 DayPlan과 연관 관계
}
