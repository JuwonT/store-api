package org.juwont.service.mapper;

import lombok.RequiredArgsConstructor;
import org.juwont.web.dto.ProductDTO;
import org.juwont.entity.Product;
import org.juwont.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public ProductDTO toDTO(final Product product) {
        final List<String> tags = product.getTags().stream()
                .map(Tag::getName)
                .toList();

        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .distribution(product.getDistribution())
                .format(product.getFormat())
                .currency(product.getCurrency())
                .price(product.getPrice())
                .releaseDate(product.getReleaseDate())
                .storeName(product.getStoreName())
                .productGroupTitle(product.getProductGroupTitle())
                .productGroupReleaseDate(product.getProductGroupReleaseDate())
                .tags(tags)
                .build();
    }
}
