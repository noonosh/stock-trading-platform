package com.stocktrading.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(nullable = false)
    @NotBlank(message = "Stock symbol is required")
    private String stockSymbol;

    @Column(nullable = false)
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be non-negative")
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Average purchase price is required")
    @PositiveOrZero(message = "Average purchase price must be non-negative")
    private BigDecimal averagePurchasePrice;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    protected Portfolio() {
        // JPA requires default constructor
    }

    public Portfolio(String userId, String stockSymbol, Integer quantity, BigDecimal averagePurchasePrice) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.stockSymbol = Objects.requireNonNull(stockSymbol, "Stock symbol cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        this.averagePurchasePrice = Objects.requireNonNull(averagePurchasePrice, "Average purchase price cannot be null");
        this.lastUpdated = LocalDateTime.now();
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (averagePurchasePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Average purchase price cannot be negative");
        }
    }

    public void addShares(Integer sharesToAdd, BigDecimal purchasePrice) {
        if (sharesToAdd == null || sharesToAdd <= 0) {
            throw new IllegalArgumentException("Shares to add must be positive");
        }
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase price must be positive");
        }

        // Calculate new average purchase price
        BigDecimal totalValue = this.averagePurchasePrice.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal newSharesValue = purchasePrice.multiply(BigDecimal.valueOf(sharesToAdd));
        BigDecimal newTotalValue = totalValue.add(newSharesValue);
        
        this.quantity += sharesToAdd;
        this.averagePurchasePrice = newTotalValue.divide(BigDecimal.valueOf(this.quantity), 2, java.math.RoundingMode.HALF_UP);
        this.lastUpdated = LocalDateTime.now();
    }

    public void removeShares(Integer sharesToRemove) {
        if (sharesToRemove == null || sharesToRemove <= 0) {
            throw new IllegalArgumentException("Shares to remove must be positive");
        }
        if (sharesToRemove > this.quantity) {
            throw new IllegalArgumentException("Cannot remove more shares than owned");
        }

        this.quantity -= sharesToRemove;
        this.lastUpdated = LocalDateTime.now();
    }

    public BigDecimal getTotalValue(BigDecimal currentStockPrice) {
        if (currentStockPrice == null) {
            throw new IllegalArgumentException("Current stock price cannot be null");
        }
        return currentStockPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public BigDecimal getTotalCost() {
        return this.averagePurchasePrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public BigDecimal getGainLoss(BigDecimal currentStockPrice) {
        return getTotalValue(currentStockPrice).subtract(getTotalCost());
    }

    public BigDecimal getGainLossPercentage(BigDecimal currentStockPrice) {
        BigDecimal totalCost = getTotalCost();
        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getGainLoss(currentStockPrice).divide(totalCost, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    // Getters
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getStockSymbol() { return stockSymbol; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getAveragePurchasePrice() { return averagePurchasePrice; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(userId, portfolio.userId) && Objects.equals(stockSymbol, portfolio.stockSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, stockSymbol);
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "userId='" + userId + '\'' +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", quantity=" + quantity +
                ", averagePurchasePrice=" + averagePurchasePrice +
                '}';
    }
} 