package com.stocktrading.application.service;

import com.stocktrading.domain.model.Portfolio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for portfolio management operations.
 * Follows Interface Segregation Principle.
 */
public interface PortfolioService {

    /**
     * Get user's complete portfolio.
     */
    List<Portfolio> getUserPortfolio(String userId);

    /**
     * Get user's holdings for a specific stock.
     */
    Optional<Portfolio> getUserStockHolding(String userId, String stockSymbol);

    /**
     * Update portfolio after a trade execution.
     */
    void updatePortfolioAfterTrade(String userId, String stockSymbol, Integer quantity, 
                                 BigDecimal price, boolean isBuy);

    /**
     * Calculate total portfolio value.
     */
    BigDecimal calculatePortfolioValue(String userId);

    /**
     * Calculate total portfolio gain/loss.
     */
    BigDecimal calculatePortfolioGainLoss(String userId);

    /**
     * Get portfolio performance summary.
     */
    PortfolioSummary getPortfolioSummary(String userId);

    /**
     * Check if user has sufficient shares to sell.
     */
    boolean hasEnoughShares(String userId, String stockSymbol, Integer quantity);

    /**
     * Portfolio summary data transfer object.
     */
    class PortfolioSummary {
        private BigDecimal totalValue;
        private BigDecimal totalCost;
        private BigDecimal totalGainLoss;
        private BigDecimal totalGainLossPercentage;
        private int totalPositions;

        public PortfolioSummary(BigDecimal totalValue, BigDecimal totalCost, 
                              BigDecimal totalGainLoss, BigDecimal totalGainLossPercentage, 
                              int totalPositions) {
            this.totalValue = totalValue;
            this.totalCost = totalCost;
            this.totalGainLoss = totalGainLoss;
            this.totalGainLossPercentage = totalGainLossPercentage;
            this.totalPositions = totalPositions;
        }

        // Getters
        public BigDecimal getTotalValue() { return totalValue; }
        public BigDecimal getTotalCost() { return totalCost; }
        public BigDecimal getTotalGainLoss() { return totalGainLoss; }
        public BigDecimal getTotalGainLossPercentage() { return totalGainLossPercentage; }
        public int getTotalPositions() { return totalPositions; }
    }
} 