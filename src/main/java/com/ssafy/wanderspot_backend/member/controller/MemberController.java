package com.ssafy.wanderspot_backend.member.controller;

import com.ssafy.wanderspot_backend.member.domain.dto.MemberDto;
import com.ssafy.wanderspot_backend.member.domain.dto.UpdateFormDto;
import com.ssafy.wanderspot_backend.member.service.MemberService;
import com.ssafy.wanderspot_backend.util.JWTUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "회원 인증 컨트롤러", description = "로그인 로그아웃, 토큰처리등 회원의 인증관련 처리하는 클래스.")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    public MemberController(MemberService memberService, JWTUtil jwtUtil) {
        super();
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "아이디 중복 확인", description = "회원가입 시 아이디 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 없음"),
            @ApiResponse(responseCode = "406", description = "중복 확인"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/idcheck")
    public ResponseEntity<?> idCheck(@RequestParam("checkid") String checkId) {
        try {
            int cnt = memberService.idCheck(checkId);
            if (cnt == 0) {
                return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 아이디입니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("이미 사용 중인 아이디입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("아이디 중복 확인 중 에러 발생");
        }
    }

    @Operation(summary = "회원가입", description = "새로운 회원을 가입시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody MemberDto memberDto) {
        try {
            memberService.joinMember(memberDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 에러 발생");
        }
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호를 이용하여 로그인 처리.")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody @Parameter(description = "로그인 시 필요한 회원정보(아이디, 비밀번호).", required = true) MemberDto memberDto) {
        log.debug("login user : {}", memberDto);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        HttpStatus status = HttpStatus.ACCEPTED;
        try {
            MemberDto loginUser = memberService.login(memberDto);
            if (loginUser != null) {
                String accessToken = jwtUtil.createAccessToken(memberDto);
                String refreshToken = jwtUtil.createRefreshToken(memberDto);
                log.debug("access token : {}", accessToken);
                log.debug("refresh token : {}", refreshToken);

//				발급받은 refresh token 을 DB에 저장.
                memberService.saveRefreshToken(loginUser.getUserId(), refreshToken);

//				JSON 으로 token 전달.
                resultMap.put("access-token", accessToken);
                resultMap.put("refresh-token", refreshToken);

                status = HttpStatus.CREATED;
            } else {
                resultMap.put("message", "아이디 또는 패스워드를 확인해 주세요.");
                status = HttpStatus.UNAUTHORIZED;
            }

        } catch (Exception e) {
            log.debug("로그인 에러 발생 : {}", e);
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @Operation(summary = "회원인증", description = "회원 정보를 담은 Token 을 반환한다.")
    @GetMapping("/info/{userId}")
    public ResponseEntity<Map<String, Object>> getInfo(
            @PathVariable("userId") @Parameter(description = "인증할 회원의 아이디.", required = true) String userId,
            @RequestHeader("Authorization") String header) {
        log.debug("userId : {}, header : {} ", userId, header);
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;
//		if (jwtUtil.checkToken(request.getHeader("Authorization"))) {
        if (jwtUtil.checkToken(header)) {
            log.info("사용 가능한 토큰!!!");
            try {
//				로그인 사용자 정보.
                MemberDto memberDto = memberService.userInfo(userId);
                resultMap.put("userInfo", memberDto);
                status = HttpStatus.OK;
            } catch (Exception e) {
                log.error("정보조회 실패 : {}", e);
                resultMap.put("message", e.getMessage());
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } else {
            log.error("사용 불가능 토큰!!!");
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @Operation(summary = "로그아웃", description = "회원 정보를 담은 Token 을 제거한다.")
    @GetMapping("/logout/{userId}")
    @Hidden
    public ResponseEntity<?> removeToken(
            @PathVariable("userId") @Parameter(description = "로그아웃 할 회원의 아이디.", required = true) String userId) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;
        try {
            memberService.deleRefreshToken(userId);
            status = HttpStatus.OK;
        } catch (Exception e) {
            log.error("로그아웃 실패 : {}", e);
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @Operation(summary = "Access Token 재발급", description = "만료된 access token 을 재발급 받는다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody MemberDto memberDto, @RequestHeader("refreshToken") String token)
            throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;
//		String token = request.getHeader("refreshToken");
        log.debug("token : {}, memberDto : {}", token, memberDto);
        if (jwtUtil.checkToken(token)) {
            if (token.equals(memberService.getRefreshToken(memberDto.getUserId()))) {
                String accessToken = jwtUtil.createAccessToken(memberDto);
                log.debug("token : {}", accessToken);
                log.debug("정상적으로 access token 재발급!!!");
                resultMap.put("access-token", accessToken);
                status = HttpStatus.CREATED;
            }
        } else {
            log.debug("refresh token 도 사용 불가!!!!!!!");
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @Operation(summary = "회원 정보 조회", description = "Access Token을 이용하여 회원 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음")
    })
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String accessToken,
                                         HttpServletRequest request) {
        try {
            log.info("header is " + request.getHeader("Authorization"));
            log.info("accessToken is " + accessToken);
            String userId = jwtUtil.getUserId(accessToken);
            MemberDto memberDto = memberService.userInfo(userId);
            return ResponseEntity.ok(memberDto);
        } catch (IllegalArgumentException e) {
            log.error("회원 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SQLException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("토큰 검증 실패2: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "회원 정보 수정", description = "Access Token을 이용하여 회원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음")
    })
    @PostMapping("/update")
    public ResponseEntity<?> updateUserInfo(@RequestHeader("Authorization") String accessToken,
                                            @RequestBody UpdateFormDto updateFormDto) {
        try {
            String userId = jwtUtil.getUserId(accessToken);
            if (!userId.equals(updateFormDto.getUserId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰의 userId와 요청의 userId가 일치하지 않습니다.");
            }
            updateFormDto.setUserId(userId);
            memberService.updateUserInfo(updateFormDto);
            return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("회원 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "회원 목록 조회", description = "검색하고자 하는 회원 id의 유사도를 통해 조회합니다.")
    public List<String> searchUserList(@RequestParam("userId") String userId) throws SQLException {
        return memberService.searchUserList(userId);
    }


}
