package org.juwont.service;

import lombok.RequiredArgsConstructor;
import org.juwont.service.mapper.ProductMapper;
import org.juwont.web.dto.ProductDTO;
import org.juwont.web.dto.ProductFilterDTO;
import org.juwont.web.dto.UpdateProductDTO;
import org.juwont.web.dto.CreateProductDTO;
import org.juwont.entity.Product;
import org.juwont.entity.Tag;
import org.juwont.repository.ProductRepository;
import org.juwont.service.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private static final String PHYSICAL = "physical";
    private static final String DIGITAL = "digital";

    private final ProductRepository repository;
    private final TagService tagService;

    private final ProductMapper productMapper;

    public ProductDTO createProduct(final CreateProductDTO request) {
        final String distribution = resolveDistribution(request.isPhysical());
        final List<Tag> tags = resolveRequestTags(request.tags());

        final Product product = Product.builder()
                .title(request.title())
                .distribution(distribution)
                .format(request.format())
                .currency(Currency.getInstance(request.currency()))
                .price(new BigDecimal(request.price()))
                .releaseDate(request.releaseDate())
                .storeName(request.storeName())
                .productGroupTitle(request.productGroupTitle())
                .productGroupReleaseDate(request.productGroupReleaseDate())
                .tags(tags)
                .build();

        return productMapper.toDTO(repository.create(product));
    }

    private List<Tag> resolveRequestTags(final List<String> tags) {
        return tags.stream()
                .map(tagService::getOrCreateTag)
                .toList();
    }

    private static String resolveDistribution(final boolean isPhysical) {
        return isPhysical ? PHYSICAL : DIGITAL;
    }

    public ProductDTO updateProduct(final Long id, final UpdateProductDTO request) {
        final Product product = repository.findProduct(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Optional.ofNullable(request.isPhysical())
                .map(ProductService::resolveDistribution)
                .ifPresent(product::setDistribution);

        if (request.title() != null) product.setTitle(request.title());
        if (request.format() != null) product.setFormat(request.format());
        if (request.currency() != null) product.setCurrency(Currency.getInstance(request.currency()));
        if (request.price() != null) product.setPrice(new BigDecimal(request.price()));
        if (request.releaseDate() != null) product.setReleaseDate(request.releaseDate());
        if (request.storeName() != null) product.setStoreName(request.storeName());
        if (request.productGroupTitle() != null) product.setProductGroupTitle(request.productGroupTitle());
        if (request.productGroupReleaseDate() != null)
            product.setProductGroupReleaseDate(request.productGroupReleaseDate());
        if (request.tags() != null) product.setTags(resolveRequestTags(request.tags()));

        return productMapper.toDTO(repository.update(product));
    }

    public List<ProductDTO> findByFilters(final ProductFilterDTO filters) {
        final List<Product> resultList = repository.filter(filters);

        return resultList.stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public void deleteById(final Long id) {
        final Product product = repository.findProduct(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        repository.delete(product);
    }
}
