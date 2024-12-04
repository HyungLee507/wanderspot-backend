package com.ssafy.wanderspot_backend.board.Repository;

import com.ssafy.wanderspot_backend.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 리스트 조회
    @Query("SELECT b FROM Board b ORDER BY b.registerTime DESC")
    List<Board> listArticles();

    @Query(value = "SELECT * FROM board b WHERE b.title LIKE %:word% ORDER BY b.register_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Board> findByTitleWithPagination(@Param("word") String word, @Param("offset") int offset,
                                          @Param("limit") int limit);

    @Query(value = "SELECT * FROM board b WHERE b.user_id = :userId ORDER BY b.register_time DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Board> findByUserIdWithPagination(@Param("userId") String userId, @Param("offset") int offset,
                                           @Param("limit") int limit);

    // 단일 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.id = :articleNo")
    Board getArticle(@Param("articleNo") Long articleNo);

    // 조회수 증가
    @Modifying
    @Query("UPDATE Board b SET b.hit = b.hit + 1 WHERE b.id = :articleNo")
    void updateHit(@Param("articleNo") Long articleNo);

    // 게시글 작성자 ID 가져오기
    @Query("SELECT b.user.userId FROM Board b WHERE b.id = :articleNo")
    String getUserIdByArticleId(@Param("articleNo") Long articleNo);

//    @Query("SELECT b FROM Board b JOIN FETCH b.fileInfos WHERE b.id = :articleNo")
//    Board findByArticleNoWithFileInfos(@Param("articleNo") Integer articleNo);

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:word%")
    List<Board> searchByTitle(@Param("word") String word);

    @Query("SELECT b FROM Board b WHERE b.user.userId = :word")
    List<Board> searchByUserId(@Param("word") String word);

    @Query("SELECT COUNT(b) FROM Board b WHERE b.title LIKE %:word%")
    int countByTitle(@Param("word") String word);

    @Query("SELECT COUNT(b) FROM Board b WHERE b.user.userId = :word")
    int countByUserId(@Param("word") String word);

    @Query("SELECT b FROM Board b JOIN Member m ON b.user.userId = m.userId ORDER BY b.id DESC")
    List<Board> findAllWithPagination(int start, int listSize);

}
