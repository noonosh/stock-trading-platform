package com.stocktrading.application.service.impl;

import com.stocktrading.application.service.TradingService;
import com.stocktrading.application.service.StockPriceService;
import com.stocktrading.application.service.PortfolioService;
import com.stocktrading.domain.model.Trade;
import com.stocktrading.domain.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TradingService.
 * Follows Single Responsibility Principle - only handles trading operations.
 * Uses Dependency Injection for loose coupling with other services.
 */
@Service
@Transactional
public class TradingServiceImpl implements TradingService {

    private final TradeRepository tradeRepository;
    private final StockPriceService stockPriceService;
    private final PortfolioService portfolioService;

    @Autowired
    public TradingServiceImpl(TradeRepository tradeRepository,
                             StockPriceService stockPriceService,
                             PortfolioService portfolioService) {
        this.tradeRepository = tradeRepository;
        this.stockPriceService = stockPriceService;
        this.portfolioService = portfolioService;
    }

    @Override
    public Trade buyStock(String userId, String stockSymbol, Integer quantity) {
        validateTradeParameters(userId, stockSymbol, quantity);
        
        String upperCaseSymbol = stockSymbol.toUpperCase();
        
        // Check if stock exists
        if (!stockPriceService.stockExists(upperCaseSymbol)) {
            throw new IllegalArgumentException("Stock not found: " + upperCaseSymbol);
        }
        
        // Get current stock price
        BigDecimal currentPrice = stockPriceService.getCurrentPrice(upperCaseSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get current price for: " + upperCaseSymbol));
        
        // Create and save trade
        Trade trade = new Trade(userId, upperCaseSymbol, Trade.TradeType.BUY, quantity, currentPrice);
        
        try {
            // Execute the trade (in a real system, this might involve order matching, etc.)
            executeTrade(trade);
            
            // Update portfolio
            portfolioService.updatePortfolioAfterTrade(userId, upperCaseSymbol, quantity, currentPrice, true);
            
            trade.markAsExecuted();
        } catch (Exception e) {
            trade.markAsFailed("Trade execution failed: " + e.getMessage());
        }
        
        return tradeRepository.save(trade);
    }

    @Override
    public Trade sellStock(String userId, String stockSymbol, Integer quantity) {
        validateTradeParameters(userId, stockSymbol, quantity);
        
        String upperCaseSymbol = stockSymbol.toUpperCase();
        
        // Check if stock exists
        if (!stockPriceService.stockExists(upperCaseSymbol)) {
            throw new IllegalArgumentException("Stock not found: " + upperCaseSymbol);
        }
        
        // Check if user has enough shares
        if (!portfolioService.hasEnoughShares(userId, upperCaseSymbol, quantity)) {
            throw new IllegalArgumentException("Insufficient shares to sell");
        }
        
        // Get current stock price
        BigDecimal currentPrice = stockPriceService.getCurrentPrice(upperCaseSymbol)
                .orElseThrow(() -> new IllegalArgumentException("Unable to get current price for: " + upperCaseSymbol));
        
        // Create and save trade
        Trade trade = new Trade(userId, upperCaseSymbol, Trade.TradeType.SELL, quantity, currentPrice);
        
        try {
            // Execute the trade
            executeTrade(trade);
            
            // Update portfolio
            portfolioService.updatePortfolioAfterTrade(userId, upperCaseSymbol, quantity, currentPrice, false);
            
            trade.markAsExecuted();
        } catch (Exception e) {
            trade.markAsFailed("Trade execution failed: " + e.getMessage());
        }
        
        return tradeRepository.save(trade);
    }

    @Override
    public List<Trade> getUserTrades(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return tradeRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Override
    public List<Trade> getUserStockTrades(String userId, String stockSymbol) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }
        return tradeRepository.findByUserIdAndStockSymbol(userId, stockSymbol.toUpperCase());
    }

    @Override
    public boolean cancelTrade(Long tradeId, String userId) {
        if (tradeId == null) {
            throw new IllegalArgumentException("Trade ID cannot be null");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        Optional<Trade> tradeOpt = tradeRepository.findById(tradeId);
        if (tradeOpt.isEmpty()) {
            return false;
        }
        
        Trade trade = tradeOpt.get();
        
        // Verify the trade belongs to the user
        if (!trade.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Trade does not belong to the specified user");
        }
        
        // Can only cancel pending trades
        if (!trade.isPending()) {
            return false;
        }
        
        trade.markAsCancelled("Cancelled by user");
        tradeRepository.save(trade);
        return true;
    }

    @Override
    public Trade getTrade(Long tradeId) {
        if (tradeId == null) {
            throw new IllegalArgumentException("Trade ID cannot be null");
        }
        return tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found with ID: " + tradeId));
    }

    @Override
    public boolean canExecuteTrade(String userId, String stockSymbol, Trade.TradeType tradeType, Integer quantity) {
        try {
            validateTradeParameters(userId, stockSymbol, quantity);
            
            String upperCaseSymbol = stockSymbol.toUpperCase();
            
            // Check if stock exists
            if (!stockPriceService.stockExists(upperCaseSymbol)) {
                return false;
            }
            
            // For sell orders, check if user has enough shares
            if (tradeType == Trade.TradeType.SELL) {
                return portfolioService.hasEnoughShares(userId, upperCaseSymbol, quantity);
            }
            
            // For buy orders, check if we can get current price
            return stockPriceService.getCurrentPrice(upperCaseSymbol).isPresent();
            
        } catch (Exception e) {
            return false;
        }
    }

    private void validateTradeParameters(String userId, String stockSymbol, Integer quantity) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be null or empty");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    /**
     * Execute the trade (mock implementation).
     * In a real system, this would involve order matching, market makers, etc.
     */
    private void executeTrade(Trade trade) {
        // Mock trade execution - in reality this would be much more complex
        // For now, we just simulate successful execution
        
        // Could add random delays, partial fills, market volatility simulation, etc.
        // For demonstration purposes, we'll just log the trade
        System.out.println("Executing trade: " + trade);
        
        // In a real system, you might:
        // 1. Submit order to exchange
        // 2. Wait for order matching
        // 3. Handle partial fills
        // 4. Update trade status based on execution results
        // 5. Handle market hours, circuit breakers, etc.
    }
} 