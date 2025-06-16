package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.BookMetrics;
import com.part3.deokhugam.domain.PopularBook;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.book.*;
import com.part3.deokhugam.dto.pagination.CursorPageResponseBookDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponsePopularBookDto;
import com.part3.deokhugam.exception.BookException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.infra.aws.s3.S3Service;
import com.part3.deokhugam.mapper.BookMapper;
import com.part3.deokhugam.mapper.PopularBookMapper;
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
  public CursorPageResponseBookDto getBooks(String keyword, Instant after, String cursor,
      String orderBy,
      String direction, int limit) {
    Set<String> orderBySet = Set.of("title", "publishedDate", "rating", "reviewCount");

    if (!orderBySet.contains(orderBy)) {
      throw new BookException(ErrorCode.INVALID_ORDER_BY, Map.of("orderBy", orderBy));
    }

    List<Book> books = bookRepository.findListByCursor(keyword, after, cursor, orderBy, direction,
        limit + 1);

    boolean hasNext = books.size() > limit;
    List<Book> pagedBooks = hasNext ? books.subList(0, limit) : books;

    List<BookDto> bookDtos = pagedBooks.stream()
        .map(bookMapper::toDto)
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext) {
      Book lastBook = books.get(limit);
      BookMetrics bookMetrics = lastBook.getBookMetrics();
      switch (orderBy) {
        case "title" -> nextCursor = lastBook.getTitle();
        case "publishedDate" -> nextCursor = lastBook.getPublishedDate().toString();
        case "rating" -> nextCursor = bookMetrics.getAverageRating().toString();
        case "reviewCount" -> nextCursor = bookMetrics.getReviewCount().toString();
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
    if (bookRepository.existsByIsbnAndDeletedFalse(request.isbn())) {
      throw new BookException(ErrorCode.BOOK_ALREADY_EXISTS,
          Map.of(
              "isbn", request.isbn(),
              "title", request.title(),
              "author", request.author()
          ));
    }

    String thumbnailUrl = null;

    if (image != null) {
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

    BookMetrics metrics = BookMetrics.builder()
        .reviewCount(0)
        .averageRating(new BigDecimal("0.0"))
        .build();

    book.setBookMetrics(metrics);

    bookRepository.save(book);

    return bookMapper.toDto(book);
  }

  public String ocrIsbn(MultipartFile image) throws IOException {
    if (image.isEmpty()) {
      throw new BookException(ErrorCode.EMPTY_IMAGE_FILE);
    }

    BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

    String result = null;
    try {
      result = tesseract.doOCR(bufferedImage);
    } catch (TesseractException e) {
      throw new BookException(ErrorCode.OCR_ERROR,
          Map.of("tesseractError", e.getMessage()));
    }
    return result.replaceAll("[^0-9]", "").trim();
  }

  @Transactional(readOnly = true)
  public BookDto getBookById(UUID bookId) {
    Book book = bookRepository.findByIdAndDeletedFalse(bookId)
        .orElseThrow(
            () -> new BookException(ErrorCode.BOOK_NOT_FOUND, Map.of("bookId", bookId.toString())));
    return bookMapper.toDto(book);
  }

  public void deleteBookLogical(UUID bookId) {
    Book book = bookRepository.findByIdAndDeletedFalse(bookId).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND, Map.of("bookId", bookId.toString())));
    book.logicalDelete();
  }

  public BookDto updateBook(UUID bookId, BookUpdateRequest bookUpdateRequest, MultipartFile file) {
    Book book = bookRepository.findByIdAndDeletedFalse(bookId)
        .orElseThrow(
            () -> new BookException(ErrorCode.BOOK_NOT_FOUND, Map.of("bookId", bookId.toString())));

    String thumbnailUrl = book.getThumbnailUrl(); // 기본적으로 기존 썸네일 유지

    if (file != null) {
      if (!file.isEmpty()) {
        thumbnailUrl = s3Service.uploadFile(file);
      } else {
        thumbnailUrl = "/images/book-placeholder.png";
      }
    }

    book.update(
        bookUpdateRequest.title(),
        bookUpdateRequest.author(),
        bookUpdateRequest.description(),
        bookUpdateRequest.publisher(),
        bookUpdateRequest.publishedDate(),
        thumbnailUrl
    );

    return bookMapper.toDto(book);
  }

  @Transactional(readOnly = true)
  public CursorPageResponsePopularBookDto getPopularBooks(String period, Instant after,
      String cursor, String direction, int limit) {
    try {
      Period.valueOf(period);
    } catch (IllegalArgumentException e) {
      throw new BookException(ErrorCode.INVALID_PERIOD, Map.of("period", period));
    }

    List<PopularBook> popularBooks = popularBookRepository.findListByCursor(period, after, cursor,
        direction,
        limit + 1);

    boolean hasNext = popularBooks.size() > limit;
    List<PopularBook> pagedBooks = hasNext ? popularBooks.subList(0, limit) : popularBooks;

    List<PopularBookDto> popularBookDtos = pagedBooks.stream()
        .map(popularBookMapper::toDto)
        .toList();

    Long totalElements = popularBookRepository.getTotalElements(period);

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext) {
      PopularBook lastPopularBook = popularBooks.get(limit);
      nextCursor = String.valueOf(lastPopularBook.getRank());
      nextAfter = lastPopularBook.getCreatedAt();
    }

    return new CursorPageResponsePopularBookDto(
        popularBookDtos,
        nextCursor,
        nextAfter,
        popularBookDtos.size(),
        totalElements,
        hasNext
    );
  }

  public void deleteBookPhysical(UUID bookId) {
    bookRepository.findById(bookId).orElseThrow(
        () -> new BookException(ErrorCode.BOOK_NOT_FOUND, Map.of("bookId", bookId.toString())));

    bookRepository.deleteById(bookId);
    // 도서 관련 리뷰 삭제 로직
  }

  public void calculateRanking(Period period) {
    LocalDate now = LocalDate.now();
    Instant start = null;
    Instant end = null;
    ZoneId UTC = ZoneOffset.UTC;

    // ===== 1. ALL_TIME은 Book → BookMetrics 기반 =====
    if (period == Period.ALL_TIME) {
      List<Book> books = bookRepository.findAll();

      List<PopularBook> allTimeRanking = books.stream()
          .filter(book ->
              !book.isDeleted() &&
                  book.getBookMetrics() != null &&
                  book.getBookMetrics().getReviewCount() > 0)
          .map(book -> {
            BookMetrics metrics = book.getBookMetrics();

            int reviewCount = metrics.getReviewCount();
            BigDecimal avgRating = metrics.getAverageRating();

            BigDecimal weightedCount = BigDecimal.valueOf(reviewCount)
                .multiply(BigDecimal.valueOf(0.4));
            BigDecimal weightedRating = avgRating.multiply(BigDecimal.valueOf(0.6));
            BigDecimal score = weightedCount.add(weightedRating);

            return PopularBook.builder()
                .period(Period.ALL_TIME)
                .periodDate(now)
                .score(score)
                .reviewCount(reviewCount)
                .book(book)
                .build();
          })
          .sorted(Comparator.comparing(PopularBook::getScore).reversed())
          .collect(Collectors.toList());

      for (int i = 0; i < allTimeRanking.size(); i++) {
        allTimeRanking.get(i).setRank(i + 1);
      }

      popularBookRepository.saveAll(allTimeRanking);
      return;
    }

    // ===== 2. 기간 기반 랭킹 계산 (DAILY, WEEKLY, MONTHLY) =====
    switch (period) {
      case DAILY:
        start = now.minusDays(1).atStartOfDay(UTC).toInstant();
        end = now.minusDays(1).atTime(LocalTime.MAX).atZone(UTC).toInstant();
        break;
      case WEEKLY:
        start = now.minusDays(7).atStartOfDay(UTC).toInstant();
        end = now.minusDays(1).atTime(LocalTime.MAX).atZone(UTC).toInstant();
        break;
      case MONTHLY:
        start = now.minusMonths(1).atStartOfDay(UTC).toInstant();
        end = now.minusDays(1).atTime(LocalTime.MAX).atZone(UTC).toInstant();
        break;
      default:
        throw new BookException(ErrorCode.INVALID_PERIOD, Map.of("period", period.name()));
    }

    List<Review> reviews = reviewRepository.findByCreatedAtBetweenAndDeletedFalse(start, end);

    Map<UUID, List<Review>> grouped = reviews.stream()
        .filter(r -> r.getBook() != null && !r.getBook().isDeleted())
        .collect(Collectors.groupingBy(r -> r.getBook().getId()));

    List<PopularBook> rankingList = grouped.entrySet().stream()
        .map(entry -> {
          UUID bookId = entry.getKey();
          List<Review> reviewList = entry.getValue();

          int reviewCount = reviewList.size();

          // Integer → BigDecimal 변환하여 평균 계산
          BigDecimal avgRating = reviewList.stream()
              .map(Review::getRating)
              .reduce(BigDecimal.ZERO, BigDecimal::add)
              .divide(BigDecimal.valueOf(reviewCount), 2, RoundingMode.HALF_UP);

          BigDecimal weightedCount = BigDecimal.valueOf(reviewCount)
              .multiply(BigDecimal.valueOf(0.4));
          BigDecimal weightedRating = avgRating.multiply(BigDecimal.valueOf(0.6));
          BigDecimal score = weightedCount.add(weightedRating);

          Book book = bookRepository.findById(bookId)
              .orElseThrow(() -> new BookException(ErrorCode.BOOK_NOT_FOUND,
                  Map.of("bookId", bookId.toString())));
          return PopularBook.builder()
              .period(period)
              .periodDate(now)
              .score(score)
              .reviewCount(reviewCount)
              .book(book)
              .build();
        })
        .sorted(Comparator.comparing(PopularBook::getScore).reversed())
        .collect(Collectors.toList());

    for (int i = 0; i < rankingList.size(); i++) {
      rankingList.get(i).setRank(i + 1);
    }

    popularBookRepository.saveAll(rankingList);
  }
}
