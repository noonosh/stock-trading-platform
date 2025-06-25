package com.stocktrading.application.service.impl;

import com.stocktrading.application.service.PortfolioService;
import com.stocktrading.application.service.StockPriceService;
import com.stocktrading.domain.model.Portfolio;
import com.stocktrading.domain.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of PortfolioService.
 * Follows Single Responsibility Principle - only handles portfolio operations.
 * Uses Dependency Injection for loose coupling.
 */
@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockPriceService stockPriceService;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                               StockPriceService stockPriceService) {
        this.portfolioRepository = portfolioRepository;
        this.stockPriceService = stockPriceService;
    }

    @Override
    public List<Portfolio> getUserPortfolio(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return portfolioRepository.findByUserId(userId);
    }

    @Override
    public Optional<Portfolio> getUserStockHolding(String userId, String stockSymbol) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }
        return portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol.toUpperCase());
    }

    @Override
    public void updatePortfolioAfterTrade(String userId, String stockSymbol, Integer quantity,
                                        BigDecimal price, boolean isBuy) {
        validateTradeParameters(userId, stockSymbol, quantity, price);
        
        String upperCaseSymbol = stockSymbol.toUpperCase();
        Optional<Portfolio> existingHolding = getUserStockHolding(userId, upperCaseSymbol);

        if (isBuy) {
            handleBuyTrade(userId, upperCaseSymbol, quantity, price, existingHolding);
        } else {
            handleSellTrade(userId, upperCaseSymbol, quantity, existingHolding);
        }
    }

    @Override
    public BigDecimal calculatePortfolioValue(String userId) {
        List<Portfolio> portfolio = getUserPortfolio(userId);
        return portfolio.stream()
                .map(holding -> {
                    BigDecimal currentPrice = stockPriceService.getCurrentPrice(holding.getStockSymbol())
                            .orElse(BigDecimal.ZERO);
                    return holding.getTotalValue(currentPrice);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculatePortfolioGainLoss(String userId) {
        List<Portfolio> portfolio = getUserPortfolio(userId);
        return portfolio.stream()
                .map(holding -> {
                    BigDecimal currentPrice = stockPriceService.getCurrentPrice(holding.getStockSymbol())
                            .orElse(BigDecimal.ZERO);
                    return holding.getGainLoss(currentPrice);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PortfolioSummary getPortfolioSummary(String userId) {
        List<Portfolio> portfolio = getUserPortfolio(userId);
        
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        
        for (Portfolio holding : portfolio) {
            BigDecimal currentPrice = stockPriceService.getCurrentPrice(holding.getStockSymbol())
                    .orElse(BigDecimal.ZERO);
            totalValue = totalValue.add(holding.getTotalValue(currentPrice));
            totalCost = totalCost.add(holding.getTotalCost());
        }
        
        BigDecimal totalGainLoss = totalValue.subtract(totalCost);
        BigDecimal totalGainLossPercentage = totalCost.compareTo(BigDecimal.ZERO) > 0 
                ? totalGainLoss.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;
        
        return new PortfolioSummary(totalValue, totalCost, totalGainLoss, 
                                  totalGainLossPercentage, portfolio.size());
    }

    @Override
    public boolean hasEnoughShares(String userId, String stockSymbol, Integer quantity) {
        Optional<Portfolio> holding = getUserStockHolding(userId, stockSymbol);
        return holding.map(portfolio -> portfolio.getQuantity() >= quantity).orElse(false);
    }

    private void validateTradeParameters(String userId, String stockSymbol, Integer quantity, BigDecimal price) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    private void handleBuyTrade(String userId, String stockSymbol, Integer quantity, 
                               BigDecimal price, Optional<Portfolio> existingHolding) {
        if (existingHolding.isPresent()) {
            Portfolio portfolio = existingHolding.get();
            portfolio.addShares(quantity, price);
            portfolioRepository.save(portfolio);
        } else {
            Portfolio newPortfolio = new Portfolio(userId, stockSymbol, quantity, price);
            portfolioRepository.save(newPortfolio);
        }
    }

    private void handleSellTrade(String userId, String stockSymbol, Integer quantity,
                                Optional<Portfolio> existingHolding) {
        if (existingHolding.isEmpty()) {
            throw new IllegalArgumentException("No holdings found for stock: " + stockSymbol);
        }

        Portfolio portfolio = existingHolding.get();
        if (portfolio.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient shares to sell");
        }

        portfolio.removeShares(quantity);
        
        if (portfolio.getQuantity() == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolioRepository.save(portfolio);
        }
    }
} 