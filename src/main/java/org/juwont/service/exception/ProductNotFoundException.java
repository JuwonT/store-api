package org.juwont.service.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Product: %s does not exit".formatted(productId));
    }
}
