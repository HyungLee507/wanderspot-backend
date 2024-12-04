package com.ssafy.wanderspot_backend.comment.repository;

import com.ssafy.wanderspot_backend.comment.domain.CommentDto;
import com.ssafy.wanderspot_backend.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new com.ssafy.wanderspot_backend.comment.domain.CommentDto(c.id, c.content, c.user.userId, c.board.id,c.registerTime) "
            +
            "FROM Comment c WHERE c.board.id = :boardId "
            + "order by c.registerTime ")
    List<CommentDto> findCommentsByBoardId(@Param("boardId") Long boardId);
    // 댓글 달기
    // 댓글 수정
    // 댓글 삭제

}
