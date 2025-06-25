package com.stocktrading.presentation.dto;

import java.math.BigDecimal;

/**
 * DTO for Portfolio Summary responses.
 */
public class PortfolioSummaryDto {

    private BigDecimal totalValue;
    private BigDecimal totalCost;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercentage;
    private int totalPositions;

    public PortfolioSummaryDto() {}

    public PortfolioSummaryDto(BigDecimal totalValue, BigDecimal totalCost,
                              BigDecimal totalGainLoss, BigDecimal totalGainLossPercentage,
                              int totalPositions) {
        this.totalValue = totalValue;
        this.totalCost = totalCost;
        this.totalGainLoss = totalGainLoss;
        this.totalGainLossPercentage = totalGainLossPercentage;
        this.totalPositions = totalPositions;
    }

    // Getters and Setters
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getTotalGainLoss() { return totalGainLoss; }
    public void setTotalGainLoss(BigDecimal totalGainLoss) { this.totalGainLoss = totalGainLoss; }

    public BigDecimal getTotalGainLossPercentage() { return totalGainLossPercentage; }
    public void setTotalGainLossPercentage(BigDecimal totalGainLossPercentage) { this.totalGainLossPercentage = totalGainLossPercentage; }

    public int getTotalPositions() { return totalPositions; }
    public void setTotalPositions(int totalPositions) { this.totalPositions = totalPositions; }
} 