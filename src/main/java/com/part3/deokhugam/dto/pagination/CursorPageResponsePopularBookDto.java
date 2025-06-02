package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.book.PopularBookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponsePopularBookDto {
    private List<PopularBookDto> content;

    private String nextCursor;

    private Instant nextAfter;

    private int size;

    private Long totalElements;

    private boolean hasNext;
}
