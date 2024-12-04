package com.ssafy.wanderspot_backend.board.Service;

import com.ssafy.wanderspot_backend.board.Repository.BoardRepository;
import com.ssafy.wanderspot_backend.board.domain.BoardDto;
import com.ssafy.wanderspot_backend.board.domain.BoardListDto;
import com.ssafy.wanderspot_backend.entity.Board;
import com.ssafy.wanderspot_backend.entity.Member;
import com.ssafy.wanderspot_backend.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void writeArticle(BoardDto boardDto) throws Exception {
        Board board = convertToEntity(boardDto);
        boardRepository.save(board);
    }

    @Override
    public BoardListDto listArticle(Map<String, String> map) throws Exception {
        String key = map.getOrDefault("key", "");
        String word = map.getOrDefault("word", "");

        int currentPage = Integer.parseInt(map.getOrDefault("pgno", "1"));
        int sizePerPage = Integer.parseInt(map.getOrDefault("spp", "20"));
        int offset = (currentPage - 1) * sizePerPage;

        List<Board> boards;

        if ("user_id".equals(key)) {
            boards = boardRepository.findByUserIdWithPagination(word, offset, sizePerPage);
        } else {
            boards = boardRepository.findByTitleWithPagination(word, offset, sizePerPage);
        }

        List<BoardDto> articles = boards.stream()
                .map(board -> {
                    BoardDto dto = new BoardDto();
                    dto.setArticleNo(board.getId());
                    dto.setUserId(board.getUser().getUserId());
                    dto.setSubject(board.getTitle());
                    dto.setContent(board.getContent());
                    dto.setHit(board.getHit());
                    dto.setRegisterTime(board.getRegisterTime());
                    return dto;
                }).collect(Collectors.toList());

        BoardListDto boardListDto = new BoardListDto();
        boardListDto.setArticles(articles);
        boardListDto.setCurrentPage(currentPage);
        boardListDto.setTotalPageCount((int) Math.ceil((double) boardRepository.count() / sizePerPage));

        return boardListDto;
    }

    @Override
    public BoardDto getArticle(long articleNo) throws Exception {
        Board board = boardRepository.getArticle((long) articleNo);
        if (board == null) {
            throw new Exception("Article not found");
        }
        // Board -> BoardDto 변환
        BoardDto boardDto = new BoardDto();
        boardDto.setArticleNo(board.getId());
        boardDto.setUserId(board.getUser().getUserId());
        boardDto.setSubject(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setHit(board.getHit());
        boardDto.setRegisterTime(board.getRegisterTime());

        return boardDto;
    }

    @Override
    @Transactional
    public void updateHit(long articleNo) throws Exception {
        boardRepository.updateHit(articleNo);
    }

    @Override
    @Transactional
    public void modifyArticle(BoardDto boardDto) throws Exception {
        Board board = boardRepository.findById((long) boardDto.getArticleNo())
                .orElseThrow(() -> new Exception("Article not found"));

        board.setTitle(boardDto.getSubject());
        board.setContent(boardDto.getContent());

        boardRepository.save(board);
    }

    @Override
    @Transactional
    public void deleteArticle(long articleNo) throws Exception {
        Board board = boardRepository.findById(articleNo)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + articleNo));
        boardRepository.delete(board);
    }

    @Override
    public String getUserIdByArticleId(Long articleNo) {
        return boardRepository.getUserIdByArticleId(articleNo);
    }

    private Board convertToEntity(BoardDto dto) {
        Board board = new Board();
        board.setId((long) dto.getArticleNo());
        board.setTitle(dto.getSubject());
        board.setContent(dto.getContent());
        board.setHit(dto.getHit());
//        board.setRegisterTime(dto.getRegisterTime());

        Member user = memberRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));
        board.setUser(user);
        return board;
    }

    private BoardDto convertToDto(Board entity) {
        BoardDto dto = new BoardDto();
        dto.setArticleNo(entity.getId());
        dto.setSubject(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setHit(entity.getHit());
        dto.setRegisterTime(entity.getRegisterTime());
        return dto;
    }
}
