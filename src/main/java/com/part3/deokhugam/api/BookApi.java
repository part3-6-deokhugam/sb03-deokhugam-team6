package com.part3.deokhugam.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.part3.deokhugam.dto.book.*;
import com.part3.deokhugam.dto.pagination.CursorPageResponseBookDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponsePopularBookDto;
import com.part3.deokhugam.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Tag(name = "도서 관리", description = "도서 관련 API")
@RequestMapping("/api/books")
public interface BookApi {

    @Operation(summary = "도서 목록 조회", description = "검색 조건에 맞는 도서 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    ResponseEntity<CursorPageResponseBookDto> getBooks(
            @Parameter(description = "도서 제목 | 저자 | ISBN", example = "자바") String keyword,
            @Parameter(description = "정렬 기준 (title | publishedDate | rating | reviewCount)", example = "title") String orderBy,
            @Parameter(description = "정렬 방향", schema = @Schema(allowableValues = {"ASC", "DESC"}, defaultValue = "ASC"), example = "DESC") String direction,
            @Parameter(description = "커서 페이지네이션 커서") String cursor,
            @Parameter(description = "보조 커서(createdAt)") Instant after,
            @Parameter(description = "페이지 크기", example = "50") int limit
    );

    @Operation(summary = "도서 등록", description = "새로운 도서를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "도서 등록 성공",
                    content = @Content(schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, ISBN 형식 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN 중복",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<BookDto> createBook(
            @Parameter(description = "도서 정보") BookCreateRequest bookCreateRequest,

            @Parameter(description = "도서 썸네일 이미지",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))) MultipartFile file
    );

    @Operation(summary = "ISBN OCR 인식", description = "도서 이미지를 통해 ISBN을 인식합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ISBN 인식 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 이미지 형식 또는 OCR 인식 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(path = "/isbn/ocr")
    ResponseEntity<String> ocrIsbn(
            @Parameter(description = "도서 이미지",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))) MultipartFile image
    ) throws TesseractException, IOException;

    @Operation(summary = "도서 상세 조회", description = "도서 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{bookId}")
    ResponseEntity<BookDto> getBookById(
            @Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID bookId
    );

    @Operation(summary = "도서 논리 삭제", description = "도서를 논리적으로 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{bookId}")
    ResponseEntity<Void> deleteBookLogical(
            @Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID bookId
    );

    @Operation(summary = "도서 정보 수정", description = "도서 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, ISBN 형식 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN 중복",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping(path = "/{bookId}")
    ResponseEntity<BookDto> updateBook(
            @Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID bookId,

            @Parameter(description = "수정할 도서 정보") BookUpdateRequest bookUpdateRequest,

            @Parameter(description = "수정할 도서 썸네일 이미지",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))) MultipartFile file
    );

    @Operation(summary = "인기 도서 조회", description = "기간별 인기 도서 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 도서 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CursorPageResponsePopularBookDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/popular")
    ResponseEntity<CursorPageResponsePopularBookDto> getPopularBooks(
            @Parameter(description = "랭킹 기간", example = "DAILY", schema = @Schema(allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"})) String period,

            @Parameter(description = "정렬 방향", example = "ASC", schema = @Schema(allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")) String direction,

            @Parameter(description = "커서 페이지네이션 커서") String cursor,

            @Parameter(description = "보조 커서(createdAt)") Instant after,

            @Parameter(description = "페이지 크기", example = "50") int limit
    );

    @Operation(summary = "ISBN 도서 정보 조회", description = "Naver API를 통해 ISBN으로 도서 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 ISBN 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/info")
    ResponseEntity<NaverBookDto> getBookInfo(
            @Parameter(description = "ISBN 번호", example = "9788965402602") String isbn
    ) throws JsonProcessingException;

    @Operation(summary = "도서 물리 삭제", description = "도서를 물리적으로 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{bookId}/hard")
    ResponseEntity<Void> deleteBookPhysical(
            @Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID bookId
    );
}