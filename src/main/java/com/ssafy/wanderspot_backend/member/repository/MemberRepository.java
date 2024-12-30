package com.ssafy.wanderspot_backend.member.repository;

import com.ssafy.wanderspot_backend.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByUserId(String userId);

    // userName으로 Member 조회
    Optional<Member> findByUserName(String userName);

    List<Member> findAllByUserIdIn(List<String> joinMemberIds);
}
