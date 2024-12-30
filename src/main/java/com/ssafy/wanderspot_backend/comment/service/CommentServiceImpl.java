package com.ssafy.wanderspot_backend.comment.service;

import com.ssafy.wanderspot_backend.board.Repository.BoardRepository;
import com.ssafy.wanderspot_backend.comment.domain.CommentDto;
import com.ssafy.wanderspot_backend.comment.repository.CommentRepository;
import com.ssafy.wanderspot_backend.entity.Board;
import com.ssafy.wanderspot_backend.entity.Comment;
import com.ssafy.wanderspot_backend.entity.Member;
import com.ssafy.wanderspot_backend.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        // Board 및 Member 객체 조회
        Board board = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + commentDto.getBoardId()));
        Member user = memberRepository.findByUserName(commentDto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + commentDto.getUserName()));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setBoard(board);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Override
    public List<CommentDto> getCommentsByBoard(Long boardId) {
        return commentRepository.findById(boardId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        comment.setContent(content);

        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserName(comment.getUser().getUserName());
        dto.setBoardId(comment.getBoard().getId());
        return dto;
    }
}
