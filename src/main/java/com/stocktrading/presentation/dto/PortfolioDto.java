package com.stocktrading.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Portfolio responses.
 */
public class PortfolioDto {

    private Long id;
    private String userId;
    private String stockSymbol;
    private Integer quantity;
    private BigDecimal averagePurchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;
    private BigDecimal totalCost;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;
    private LocalDateTime lastUpdated;

    public PortfolioDto() {}

    public PortfolioDto(Long id, String userId, String stockSymbol, Integer quantity,
                       BigDecimal averagePurchasePrice, BigDecimal currentPrice,
                       BigDecimal totalValue, BigDecimal totalCost, BigDecimal gainLoss,
                       BigDecimal gainLossPercentage, LocalDateTime lastUpdated) {
        this.id = id;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.averagePurchasePrice = averagePurchasePrice;
        this.currentPrice = currentPrice;
        this.totalValue = totalValue;
        this.totalCost = totalCost;
        this.gainLoss = gainLoss;
        this.gainLossPercentage = gainLossPercentage;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getAveragePurchasePrice() { return averagePurchasePrice; }
    public void setAveragePurchasePrice(BigDecimal averagePurchasePrice) { this.averagePurchasePrice = averagePurchasePrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getGainLoss() { return gainLoss; }
    public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }

    public BigDecimal getGainLossPercentage() { return gainLossPercentage; }
    public void setGainLossPercentage(BigDecimal gainLossPercentage) { this.gainLossPercentage = gainLossPercentage; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
} 