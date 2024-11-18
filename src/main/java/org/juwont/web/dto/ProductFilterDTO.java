package org.juwont.web.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ProductFilterDTO (
    String storeName,
    String groupTitle,
    Instant releaseDateBefore,
    Instant releaseDateAfter,
    String title,
    Instant groupReleaseDateBefore,
    Instant groupReleaseDateAfter,
    List<String> tags,
    Integer page,
    Integer size
) {
    public ProductFilterDTO {
        if (page == null) page = 1;
        if (size == null) size = 10;
    }
}
