package com.ssafy.wanderspot_backend.member.mapper;

import com.ssafy.wanderspot_backend.member.domain.dto.MemberDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateFormDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateUserDto;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {


    int idCheck(String userId) throws SQLException;

    int joinMember(MemberDto memberDto) throws SQLException;

    MemberDto login(MemberDto memberDto) throws SQLException;

    MemberDto userInfo(String userId) throws SQLException;

    void saveRefreshToken(Map<String, String> map) throws SQLException;

    Object getRefreshToken(String userid) throws SQLException;

    void deleteRefreshToken(Map<String, String> map) throws SQLException;

    int updateUserInfo(UpdateFormDto updateFormDto) throws SQLException;

    UpdateUserDto getUserInfo(String userId) throws SQLException;

    List<String> searchUserList(String userId) throws SQLException;


}