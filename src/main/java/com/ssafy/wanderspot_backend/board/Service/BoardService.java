package com.ssafy.wanderspot_backend.board.Service;

import com.ssafy.wanderspot_backend.board.domain.BoardDto;
import com.ssafy.wanderspot_backend.board.domain.BoardListDto;
import java.util.Map;

public interface BoardService {

    void writeArticle(BoardDto boardDto) throws Exception;

    BoardListDto listArticle(Map<String, String> map) throws Exception;

    BoardDto getArticle(long articleNo) throws Exception;

    void updateHit(long articleNo) throws Exception;

    void modifyArticle(BoardDto boardDto) throws Exception;

    void deleteArticle(long articleNo) throws Exception;

    String getUserIdByArticleId(Long articleNo);
}
