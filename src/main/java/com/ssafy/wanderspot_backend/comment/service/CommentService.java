package com.ssafy.wanderspot_backend.comment.service;

import com.ssafy.wanderspot_backend.comment.domain.CommentDto;
import java.util.List;

public interface CommentService {

    // 댓글 생성
    CommentDto createComment(CommentDto commentDto);

    // 게시글 ID를 기준으로 댓글 조회
    List<CommentDto> getCommentsByBoard(Long boardId);

    // 작성자 이름을 기준으로 댓글 조회
//    List<CommentDto> getCommentsByUser(String userName);

    // 댓글 수정
    CommentDto updateComment(Long commentId, String content);

    // 댓글 삭제
    void deleteComment(Long commentId);
}
