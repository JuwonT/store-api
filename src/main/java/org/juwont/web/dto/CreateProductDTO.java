package org.juwont.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.juwont.domain.MediaFormat;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record CreateProductDTO(
        @NotNull(message = "must not be null") String title,
        @NotNull(message = "must not be null") Boolean isPhysical,
        @NotNull(message = "must not be null") MediaFormat format,
        @NotNull(message = "must not be null")
        @Pattern(regexp = "^(GBP|EUR|USD)$", message = "You can only choose currencies: GBP, EUR & USD")
        String currency,
        @NotNull(message = "must not be null")
        @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "value should only contain digits and 2 decimal place")
        String price,
        @NotNull(message = "must not be null") Instant releaseDate,
        @NotNull(message = "must not be null") String storeName,
        @NotNull(message = "must not be null") String productGroupTitle,
        @NotNull(message = "must not be null") Instant productGroupReleaseDate,
        @NotNull(message = "must not be null") List<String> tags
) {}
