package org.juwont.web.dto;

import lombok.Builder;
import org.juwont.domain.MediaFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;

@Builder
public record ProductDTO(Long id,
                         String title,
                         String distribution,
                         MediaFormat format,
                         Currency currency,
                         BigDecimal price,
                         Instant releaseDate,
                         String storeName,
                         String productGroupTitle,
                         Instant productGroupReleaseDate,
                         List<String> tags) {}
