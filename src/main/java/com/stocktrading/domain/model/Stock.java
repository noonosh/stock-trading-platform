package com.stocktrading.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Stock symbol is required")
    private String symbol;

    @Column(nullable = false)
    @NotBlank(message = "Company name is required")
    private String companyName;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Current price is required")
    @Positive(message = "Current price must be positive")
    private BigDecimal currentPrice;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(precision = 5, scale = 2)
    private BigDecimal changePercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal openPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal highPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal lowPrice;

    @Column
    private Long volume;

    protected Stock() {
        // JPA requires default constructor
    }

    public Stock(String symbol, String companyName, BigDecimal currentPrice) {
        this.symbol = Objects.requireNonNull(symbol, "Symbol cannot be null");
        this.companyName = Objects.requireNonNull(companyName, "Company name cannot be null");
        this.currentPrice = Objects.requireNonNull(currentPrice, "Current price cannot be null");
        this.lastUpdated = LocalDateTime.now();
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        
        if (this.currentPrice != null) {
            BigDecimal change = newPrice.subtract(this.currentPrice);
            this.changePercentage = change.divide(this.currentPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        this.currentPrice = newPrice;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getCompanyName() { return companyName; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public BigDecimal getChangePercentage() { return changePercentage; }
    public BigDecimal getOpenPrice() { return openPrice; }
    public BigDecimal getHighPrice() { return highPrice; }
    public BigDecimal getLowPrice() { return lowPrice; }
    public Long getVolume() { return volume; }

    // Setters for daily data
    public void setDailyData(BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, Long volume) {
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                ", changePercentage=" + changePercentage +
                '}';
    }
} 