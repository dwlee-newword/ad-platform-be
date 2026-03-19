package com.platform.ad.ad_platform.common.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final long totalCount;
    private final int totalPages;
    private final int page;
    private final int size;

    private PageResponse(List<T> content, long totalCount, int totalPages, int page, int size) {
        this.content = content;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    public static <T> PageResponse<T> of(List<T> content, long totalCount, int totalPages, int page, int size) {
        return new PageResponse<>(content, totalCount, totalPages, page, size);
    }
}
