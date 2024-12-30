package com.ssafy.wanderspot_backend.member.service;


import com.ssafy.wanderspot_backend.member.domain.dto.MemberDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateFormDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateUserDto;
import java.sql.SQLException;
import java.util.List;

public interface MemberService {

    MemberDto login(MemberDto memberDto) throws Exception;

    MemberDto userInfo(String userId) throws Exception;

    void saveRefreshToken(String userId, String refreshToken) throws Exception;

    Object getRefreshToken(String userId) throws Exception;

    void deleRefreshToken(String userId) throws Exception;

    int idCheck(String userId) throws SQLException;

    int joinMember(MemberDto memberDto) throws SQLException;

    int updateUserInfo(UpdateFormDto updateFormDto) throws SQLException;

    UpdateUserDto getUserInfo(String userId) throws SQLException;

    List<String> searchUserList(String userId) throws SQLException;

}
