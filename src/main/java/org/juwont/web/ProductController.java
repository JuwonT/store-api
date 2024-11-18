package org.juwont.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.juwont.web.dto.CreateProductDTO;
import org.juwont.web.dto.ProductDTO;
import org.juwont.web.dto.ProductFilterDTO;
import org.juwont.service.ProductService;
import org.juwont.web.dto.UpdateProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/product/create")
    public ProductDTO createProduct(@RequestBody @Valid final CreateProductDTO request) {
        return productService.createProduct(request);
    }

    @DeleteMapping("/api/product/{id}/delete")
    public ResponseEntity<String> deleteProduct(@PathVariable final Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/product/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable final Long id,
                                                    @RequestBody @Valid final UpdateProductDTO updateProductDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, updateProductDTO));
    }

    @GetMapping("/api/products")
    public List<ProductDTO> getProductsByFilters(@RequestParam(value = "store_name", required = false) String storeName,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(name = "group_title", required = false) String groupTitle,
                                                 @RequestParam(name = "release_date_before", required = false) Instant releaseDateBefore,
                                                 @RequestParam(name = "release_date_after", required = false) Instant releaseDateAfter,
                                                 @RequestParam(name = "group_release_date_before", required = false) Instant groupReleaseDateBefore,
                                                 @RequestParam(name = "group_release_date_after", required = false) Instant groupReleaseDateAfter,
                                                 @RequestParam(name = "tags", required = false) List<String> tags,
                                                 @RequestParam(name = "page", required = false) Integer page,
                                                 @RequestParam(name = "size", required = false) Integer size) {

        final ProductFilterDTO filters = ProductFilterDTO.builder()
                .storeName(storeName)
                .title(title)
                .releaseDateBefore(releaseDateBefore)
                .releaseDateAfter(releaseDateAfter)
                .groupTitle(groupTitle)
                .groupReleaseDateBefore(groupReleaseDateBefore)
                .groupReleaseDateAfter(groupReleaseDateAfter)
                .tags(tags)
                .page(page)
                .size(size)
                .build();

        return productService.findByFilters(filters);
    }
}
