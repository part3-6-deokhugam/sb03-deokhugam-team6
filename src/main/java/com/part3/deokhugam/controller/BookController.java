package com.part3.deokhugam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.part3.deokhugam.api.BookApi;
import com.part3.deokhugam.dto.book.BookCreateRequest;
import com.part3.deokhugam.dto.book.BookDto;
import com.part3.deokhugam.dto.book.BookUpdateRequest;
import com.part3.deokhugam.dto.book.NaverBookDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponseBookDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponsePopularBookDto;
import com.part3.deokhugam.infra.naver.NaverBookClinet;
import com.part3.deokhugam.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController implements BookApi {
    private final BookService bookService;
    private final NaverBookClinet naverBookClient;

    @Override
    public ResponseEntity<CursorPageResponseBookDto> getBooks(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "orderBy", defaultValue = "title", required = false) String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC", required = false) String direction,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "after", required = false) Instant after,
            @RequestParam(value = "limit", defaultValue = "50", required = false) int limit) {
        CursorPageResponseBookDto books = bookService.getBooks(keyword, after, cursor, orderBy, direction, limit);
        return ResponseEntity.ok(books);
    }

    @Override
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<BookDto> createBook(@RequestPart("bookData") @Valid BookCreateRequest bookCreateRequest,
                                              @RequestPart(value = "thumbnailImage", required = false) MultipartFile file) {
        BookDto createdBook = bookService.createBook(bookCreateRequest, file);
        return ResponseEntity.ok(createdBook);
    }

    @Override
    @PostMapping(value = "/isbn/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ocrIsbn(@RequestParam("image") MultipartFile image) throws TesseractException, IOException {
        String isbn = bookService.ocrIsbn(image);
        return ResponseEntity.ok(isbn);
    }
}
