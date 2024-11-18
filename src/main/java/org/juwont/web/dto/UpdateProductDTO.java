package org.juwont.web.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.juwont.domain.MediaFormat;

import java.time.Instant;
import java.util.List;

@Builder
public record UpdateProductDTO(
        String title,
        Boolean isPhysical,
        MediaFormat format,
        @Pattern(regexp = "^(GBP|EUR|USD)$", message = "You can only choose currencies: GBP, EUR & USD")
        String currency,
        @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "value should only contain digits and 2 decimal place")
        String price,
        Instant releaseDate,
        String storeName,
        String productGroupTitle,
        Instant productGroupReleaseDate,
        List<String> tags
) {}
