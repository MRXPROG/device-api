package com.example.device.api.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationUtilsTest {

    @Test
    void offsetPagination_NormalValues_CorrectPageable() {
        Pageable pageable = PaginationUtils.offsetPagination(40, 20);

        assertEquals(2, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals("createdAt: DESC", pageable.getSort().toString());
    }

    @Test
    void offsetPagination_ZeroOffset_FirstPage() {
        Pageable pageable = PaginationUtils.offsetPagination(0, 20);

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }

    @Test
    void offsetPagination_OffsetNotMultipleOfLimit_FloorDivision() {
        Pageable pageable = PaginationUtils.offsetPagination(45, 20);

        assertEquals(2, pageable.getPageNumber());
    }

    @Test
    void offsetPagination_NegativeOffset_SetsToZero() {
        Pageable pageable = PaginationUtils.offsetPagination(-10, 20);

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }

    @Test
    void offsetPagination_ZeroLimit_DefaultsTo100() {
        Pageable pageable = PaginationUtils.offsetPagination(0, 0);

        assertEquals(0, pageable.getPageNumber());
        assertEquals(100, pageable.getPageSize());
    }

    @Test
    void offsetPagination_NegativeLimit_DefaultsTo100() {
        Pageable pageable = PaginationUtils.offsetPagination(200, -5);

        assertEquals(2, pageable.getPageNumber());
        assertEquals(100, pageable.getPageSize());
    }

    @Test
    void offsetPagination_SortIsCreatedAtDesc() {
        Pageable pageable = PaginationUtils.offsetPagination(0, 50);

        Sort sort = pageable.getSort();

        assertTrue(Objects.requireNonNull(sort.getOrderFor("createdAt")).isDescending());
        assertEquals("createdAt", sort.iterator().next().getProperty());
    }
}

