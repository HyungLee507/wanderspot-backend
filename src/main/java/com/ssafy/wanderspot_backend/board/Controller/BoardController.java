package com.ssafy.wanderspot_backend.board.Controller;

import com.ssafy.wanderspot_backend.board.Service.BoardService;
import com.ssafy.wanderspot_backend.board.domain.BoardDto;
import com.ssafy.wanderspot_backend.board.domain.BoardListDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.dto.TravelPlanReviewDto;
import com.ssafy.wanderspot_backend.trip.tripPlan.service.TravelPlanService;
import com.ssafy.wanderspot_backend.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.charset.Charset;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@Tag(name = "게시판 컨트롤러", description = "게시판에 글을 등록, 수정, 삭제, 목록, 상세보기등 전반적인 처리를 하는 클래스.")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final JWTUtil jwtUtil;
    private final TravelPlanService travelPlanService;

    @Operation(summary = "게시판 글작성", description = "새로운 게시글 정보를 입력한다.")
    @PostMapping
    public ResponseEntity<?> writeArticle(@RequestHeader("Authorization") String accessToken,
                                          @RequestBody @Parameter(description = "작성글 정보.", required = true) BoardDto boardDto) {
        log.info("writeArticle boardDto - {}", boardDto);
        String userId = jwtUtil.getUserId(accessToken);
        boardDto.setUserId(userId);
        log.info("regist dto is " + boardDto);
        try {
            boardService.writeArticle(boardDto);
//			return ResponseEntity.ok().build();
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    @Operation(summary = "게시판 글목록", description = "모든 게시글의 정보를 반환한다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "게시글목록 OK!!"),
            @ApiResponse(responseCode = "404", description = "페이지없어!!"),
            @ApiResponse(responseCode = "500", description = "서버에러!!")})
    @GetMapping
    public ResponseEntity<?> listArticle(
            @RequestParam @Parameter(description = "게시글을 얻기위한 부가정보.", required = true) Map<String, String> map) {
        log.info("listArticle map - {}", map);
        try {
            BoardListDto boardListDto = boardService.listArticle(map);
            log.info("list is " + boardListDto);
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            return ResponseEntity.ok().headers(header).body(boardListDto);
        } catch (Exception e) {
            return exceptionHandling(e);
        }
    }

    @Operation(summary = "게시판 글보기", description = "글번호에 해당하는 게시글의 정보를 반환한다.")
    @GetMapping("/{articleno}")
    public ResponseEntity<BoardDto> getArticle(
            @PathVariable("articleno") @Parameter(name = "articleno", description = "얻어올 글의 글번호.", required = true) long articleno)
            throws Exception {
        log.info("getArticle - 호출 : " + articleno);
        boardService.updateHit(articleno);
        return new ResponseEntity<BoardDto>(boardService.getArticle(articleno), HttpStatus.OK);
    }

    @Operation(summary = "수정 할 글 얻기", description = "글번호에 해당하는 게시글의 정보를 반환한다.")
    @GetMapping("/modify/{articleno}")
    public ResponseEntity<BoardDto> getModifyArticle(@RequestHeader("Authorization") String accessToken,
                                                     @PathVariable("articleno") @Parameter(name = "articleno", description = "얻어올 글의 글번호.", required = true) long articleno)
            throws Exception {
        log.info("getModifyArticle - 호출 : " + articleno);
        // JWT에서 userId 추출
        String userIdFromToken = jwtUtil.getUserId(accessToken);

        // DB에서 게시글 정보 조회
        BoardDto boardDto = boardService.getArticle(articleno);
        String userIdByArticleId = boardService.getUserIdByArticleId(articleno);
        // userId 비교
        if (!userIdFromToken.equals(userIdByArticleId)) {
            log.warn("Unauthorized access: Token userId {} does not match BoardDto userId {}", userIdFromToken,
                    boardDto.getUserId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<BoardDto>(boardService.getArticle(articleno), HttpStatus.OK);
    }

    @Operation(summary = "게시판 글수정", description = "수정할 게시글 정보를 입력한다. 그리고 DB수정 성공여부에 따라 'success' 또는 'fail' 문자열을 반환한다.")
    @PutMapping
    public ResponseEntity<String> modifyArticle(@RequestHeader("Authorization") String accessToken,
                                                @RequestBody @Parameter(description = "수정할 글정보.", required = true) BoardDto boardDto)
            throws Exception {
        log.info("modifyArticle - 호출 {}", boardDto);
        // JWT에서 userId 추출
        String userIdFromToken = jwtUtil.getUserId(accessToken);
        String userIdFromBoard = boardService.getUserIdByArticleId(boardDto.getArticleNo());

        // BoardDto의 userId와 JWT의 userId 비교
        if (!userIdFromToken.equals(userIdFromBoard)) {
            log.warn("Unauthorized access: Token userId {} does not match BoardDto userId {}", userIdFromToken,
                    boardDto.getUserId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User mismatch");
        }
        boardService.modifyArticle(boardDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시판 글삭제", description = "글번호에 해당하는 게시글의 정보를 삭제한다. 그리고 DB삭제 성공여부에 따라 'success' 또는 'fail' 문자열을 반환한다.")
    @DeleteMapping("/{articleno}")
    public ResponseEntity<String> deleteArticle(@RequestHeader("Authorization") String accessToken,
                                                @PathVariable("articleno") @Parameter(name = "articleno", description = "살제할 글의 글번호.", required = true) long articleno)
            throws Exception {
        log.info("deleteArticle - 호출");

        // JWT에서 userId 추출
        String userIdFromToken = jwtUtil.getUserId(accessToken);
        String userIdByArticleId = boardService.getUserIdByArticleId(articleno);

        // DB에서 삭제할 글 정보 조회
//        BoardDto boardDto = boardService.getArticle(articleno);

        // userId 비교
        if (!userIdFromToken.equals(userIdByArticleId)) {
            log.warn("Unauthorized access: Token userId {} does not match BoardDto userId ", userIdFromToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User mismatch");
        }

        boardService.deleteArticle(articleno);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> exceptionHandling(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<String>("Error : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/planList")
    @Operation(
            summary = "여행 계획 목록 조회",
            description = "페이징 처리된 여행 계획 목록을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    array = @ArraySchema(schema = @Schema(implementation = TravelPlanReviewDto.class))
                            )
                    )
            }
    )
    public Page<TravelPlanReviewDto> getTravelPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return travelPlanService.getTravelPlans(page, size);
    }


}
