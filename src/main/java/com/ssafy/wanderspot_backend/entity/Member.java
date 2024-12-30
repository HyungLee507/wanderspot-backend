package com.ssafy.wanderspot_backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "members")
public class Member {

    @Id
    @Column(name = "user_id") // 테이블 컬럼 이름과 매핑
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_password", nullable = false)
    private String userPwd;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name = "email_domain", nullable = false)
    private String emailDomain;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "join_date", nullable = false, updatable = false)
    private Date joinDate;

    @OneToMany(mappedBy = "createUser")
    private List<TravelPlan> createdPlans; // 작성한 여행 계획

    @ManyToMany(mappedBy = "joinMembers")
    private List<TravelPlan> joinedPlans; // 참여 중인 여행 계획

    @OneToMany(mappedBy = "user")
    private List<Board> posts;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "friendship",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Member> friends;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
}
