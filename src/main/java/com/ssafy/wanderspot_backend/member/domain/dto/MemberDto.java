package com.ssafy.wanderspot_backend.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Schema(title = "MemberDto : 회원정보", description = "회원의 상세 정보를 나타낸다.")
public class MemberDto {

    @Schema(description = "아이디")
    private String userId;
    @Schema(description = "이름")
    private String userName;
    @Schema(description = "비밀번호")
    private String userPwd;
    @Schema(description = "이메일 아이디")
    private String emailId;
    @Schema(description = "이메일 도메인")
    private String emailDomain;
    @Schema(description = "가입일")
    private String joinDate;
    @Schema(description = "refreshToken")
    private String refreshToken;

}
