package com.ssafy.wanderspot_backend.comment.controller;

import com.ssafy.wanderspot_backend.comment.domain.CommentDto;
import com.ssafy.wanderspot_backend.comment.service.CommentService;
import com.ssafy.wanderspot_backend.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@Tag(name = "댓글 컨트롤러", description = "게시판 글에 댓글을 등록, 수정, 삭제하는 API")
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JWTUtil jwtUtil;

    /**
     * 특정 게시글의 댓글 조회
     *
     * @param boardId 게시글 ID
     * @return 게시글에 작성된 댓글 목록
     */
    @Operation(summary = "게시글 댓글 조회", description = "특정 게시글에 작성된 모든 댓글을 조회합니다.")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDto>> getCommentsByBoard(@PathVariable Long boardId) {
        List<CommentDto> comments = commentService.getCommentsByBoard(boardId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 생성
     *
     * @param commentDto 댓글 데이터
     * @return 생성된 댓글 정보
     */
    @Operation(summary = "댓글 생성", description = "게시판 글에 댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestHeader("Authorization") String accessToken,
                                                    @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.createComment(commentDto);
        return ResponseEntity.ok(createdComment);
    }

    /**
     * 댓글 수정
     *
     * @param commentId 수정할 댓글의 ID
     * @param content   수정할 내용
     * @return 수정된 댓글 정보
     */
    @Operation(summary = "댓글 수정", description = "특정 댓글의 내용을 수정합니다.")
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@RequestHeader("Authorization") String accessToken,
                                                    @PathVariable Long commentId, @RequestBody String content) {
        CommentDto updatedComment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글의 ID
     * @return 삭제 상태
     */
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("Authorization") String accessToken,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
