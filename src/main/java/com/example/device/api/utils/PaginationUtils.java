package com.example.device.api.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public final class PaginationUtils {

    /**
     * Creates an offset-based pageable sorted by createdAt DESC.
     *
     * @param offset number of items to skip
     * @param limit  max number of items to return
     * @return Pageable with proper calculated page index
     */
    public static Pageable offsetPagination(int offset, int limit) {
        if (limit <= 0) {
            limit = 100;
        }
        if (offset < 0) {
            offset = 0;
        }

        int page = offset / limit;

        return PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }
}
