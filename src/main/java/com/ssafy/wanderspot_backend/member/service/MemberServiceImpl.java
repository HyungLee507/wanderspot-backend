package com.ssafy.wanderspot_backend.member.service;


import com.ssafy.wanderspot_backend.member.domain.dto.MemberDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateFormDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateUserDto;
import com.ssafy.wanderspot_backend.member.mapper.MemberMapper;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    public MemberServiceImpl(MemberMapper memberMapper) {
        super();
        this.memberMapper = memberMapper;
    }

    @Override
    public int idCheck(String userId) throws SQLException {
        return memberMapper.idCheck(userId);
    }

    @Override
    public int joinMember(MemberDto memberDto) throws SQLException {
        log.info("회원 가입 로직 진입");
        return memberMapper.joinMember(memberDto);
    }

    @Override
    public int updateUserInfo(UpdateFormDto updateFormDto) throws SQLException {
        return memberMapper.updateUserInfo(updateFormDto);
    }

    @Override
    public UpdateUserDto getUserInfo(String userId) throws SQLException {
        return memberMapper.getUserInfo(userId);
    }

    @Override
    public List<String> searchUserList(String userId) throws SQLException {
        return memberMapper.searchUserList(userId);
    }

    @Override
    public MemberDto login(MemberDto memberDto) throws Exception {
        log.info("로그인 진입");
        return memberMapper.login(memberDto);
    }

    @Override
    public MemberDto userInfo(String userId) throws Exception {
        return memberMapper.userInfo(userId);
    }

    @Override
    public void saveRefreshToken(String userId, String refreshToken) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("token", refreshToken);
        memberMapper.saveRefreshToken(map);
    }

    @Override
    public Object getRefreshToken(String userId) throws Exception {
        return memberMapper.getRefreshToken(userId);
    }

    @Override
    public void deleRefreshToken(String userId) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("token", null);
        memberMapper.deleteRefreshToken(map);
    }


}
