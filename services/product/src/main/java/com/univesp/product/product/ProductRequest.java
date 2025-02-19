package com.univesp.product.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        Integer id,
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "Description is required")
        String description,
        @Positive(message = "Available quantity must be greater than zero")
        double availableQuantity,
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,
        @NotNull(message = "Category ID is required")
        Integer categoryId

) {
}
