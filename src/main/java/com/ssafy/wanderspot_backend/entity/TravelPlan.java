package com.ssafy.wanderspot_backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String title; // 여행 이름

    @Column(nullable = true)
    private String location; // 여행 위치

    @Column(columnDefinition = "TEXT")
    private String content; // 여행 설명

    @Embedded
    private PlanDate planDate; // 여행 시작/종료 날짜

    @ManyToOne
    @JoinColumn(name = "create_user_id", nullable = false)
    private Member createUser; // 작성자

    @ManyToMany
    @JoinTable(
            name = "travel_plan_members",
            joinColumns = @JoinColumn(name = "travel_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> joinMembers; // 참여 멤버들

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "travel_plan_day_plan_id")
    private List<DayPlan> dayPlanList; // 일정 리스트

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "travel_plan_day_house_id")
    private List<DayPlan> dayHouseList; // 숙소 리스트
}