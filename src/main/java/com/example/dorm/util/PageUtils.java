package com.example.dorm.util;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class PageUtils {

    private PageUtils() {
        // Utility class
    }

    public static List<Integer> buildPageNumbers(Page<?> page) {
        int totalPages = page.getTotalPages();
        if (totalPages <= 0) {
            return Collections.emptyList();
        }
        return IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();
    }
}
