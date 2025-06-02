package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.PopularBook;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.book.*;
import com.part3.deokhugam.dto.pagination.CursorPageResponseBookDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponsePopularBookDto;
import com.part3.deokhugam.exception.BookException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.infra.aws.s3.S3Service;
import com.part3.deokhugam.repository.BookRepository;
import com.part3.deokhugam.repository.PopularBookRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final PopularBookRepository popularBookRepository;
    private final S3Service s3Service;
    private final Tesseract tesseract;
    private final ReviewRepository reviewRepository;
    private final BookMapper bookMapper;
    private final PopularBookMapper popularBookMapper;

    @Transactional(readOnly = true)
    public CursorPageResponseBookDto getBooks(String keyword, Instant after, String cursor, String orderBy,
                                              String direction, int limit) {
        Set<String> orderBySet = Set.of("title", "publishedDate", "rating", "reviewCount");

        if (!orderBySet.contains(orderBy)) {
            throw new BookException(ErrorCode.INVALID_INPUT_VALUE, "Invalid orderBy value: " + orderBy);
        }

        List<Book> books = bookRepository.findListByCursor(keyword, after, cursor, orderBy, direction, limit + 1);

        boolean hasNext = books.size() > limit;
        List<Book> pagedBooks = hasNext ? books.subList(0, limit) : books;

        List<BookDto> bookDtos = pagedBooks.stream()
                .map(bookMapper::toDto)
                .toList();

        String nextCursor = null;
        Instant nextAfter = null;

        if (hasNext) {
            Book lastBook = books.get(limit);
            switch (orderBy) {
                case "title" -> nextCursor = lastBook.getTitle();
                case "publishedDate" -> nextCursor = lastBook.getPublishedDate().toString();
                case "rating" -> nextCursor = lastBook.getRating().toString();
                case "reviewCount" -> nextCursor = lastBook.getReviewCount().toString();
            }
            nextAfter = bookDtos.get(bookDtos.size() - 1).createdAt();
        }

        Long totalElements = bookRepository.getTotalElements(keyword);

        return new CursorPageResponseBookDto(
                bookDtos,
                nextCursor,
                nextAfter,
                bookDtos.size(),
                totalElements,
                hasNext
        );
    }

    public BookDto createBook(BookCreateRequest request, MultipartFile image) {
        if(bookRepository.existsByIsbn(request.isbn())) {
            throw new BookException(ErrorCode.BOOK_ALREADY_EXISTS, "Book with ISBN already exists.");
        }

        String thumbnailUrl = null;

        if(image != null) {
            thumbnailUrl = s3Service.uploadFile(image);
        }

        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .publisher(request.publisher())
                .publishedDate(request.publishedDate())
                .thumbnailUrl(thumbnailUrl)
                .isbn(request.isbn())
                .build();

        bookRepository.save(book);

        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPublisher(),
                book.getPublishedDate(),
                book.getIsbn(),
                thumbnailUrl,
                book.getReviewCount(),
                book.getRating(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    public String ocrIsbn(MultipartFile image) throws IOException {
        if(image.isEmpty()) {
            throw new BookException(ErrorCode.INVALID_INPUT_VALUE, "image file is empty.");
        }

        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

        String result = null;
        try {
            result = tesseract.doOCR(bufferedImage);
        } catch (TesseractException e) {
            throw new BookException(ErrorCode.OCR_ERROR, "OCR processing failed: " + e.getMessage());
        }
        return result.replaceAll("[^0-9]", "").trim();
    }

    @Transactional(readOnly = true)
    public BookDto getBookById(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(ErrorCode.BOOK_NOT_FOUND, "Book not found with ID: " + bookId));
        return bookMapper.toDto(book);
    }

    public void deleteBookLogical(UUID bookId) {
        Book book = bookRepository.findByIdAndDeletedFalse(bookId).orElseThrow(
                () -> new BookException(ErrorCode.BOOK_NOT_FOUND, "Book not found with ID: " + bookId));
        book.logicalDelete();
    }
}
