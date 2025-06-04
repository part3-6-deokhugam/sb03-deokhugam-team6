package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.book.BookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class CursorPageResponseBookDto {
    private List<BookDto> content;

    private String nextCursor;

    private Instant nextAfter;

    private int size;

    private long totalElements;

    private boolean hasNext;

}
