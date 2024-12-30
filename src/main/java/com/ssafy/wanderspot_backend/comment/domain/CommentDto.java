package com.ssafy.wanderspot_backend.comment.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "댓글 데이터 dto")
public class CommentDto {
    private Long id;
    private String content;
    private String userName; // 작성자 이름
    private Long boardId;
    @Schema(description = "작성일")
    private LocalDateTime registerTime;
}
