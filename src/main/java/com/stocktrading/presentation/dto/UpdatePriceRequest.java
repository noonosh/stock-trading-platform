package com.stocktrading.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for updating stock price requests.
 */
public class UpdatePriceRequest {

    @NotNull(message = "New price is required")
    @Positive(message = "New price must be positive")
    private BigDecimal newPrice;

    public UpdatePriceRequest() {}

    public UpdatePriceRequest(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
} 