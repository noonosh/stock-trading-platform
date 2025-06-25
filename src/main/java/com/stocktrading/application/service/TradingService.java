package com.stocktrading.application.service;

import com.stocktrading.domain.model.Trade;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for trading operations.
 * Follows Interface Segregation Principle.
 */
public interface TradingService {

    /**
     * Execute a buy order for a stock.
     */
    Trade buyStock(String userId, String stockSymbol, Integer quantity);

    /**
     * Execute a sell order for a stock.
     */
    Trade sellStock(String userId, String stockSymbol, Integer quantity);

    /**
     * Get all trades for a specific user.
     */
    List<Trade> getUserTrades(String userId);

    /**
     * Get trade history for a specific user and stock.
     */
    List<Trade> getUserStockTrades(String userId, String stockSymbol);

    /**
     * Cancel a pending trade.
     */
    boolean cancelTrade(Long tradeId, String userId);

    /**
     * Get a specific trade by ID.
     */
    Trade getTrade(Long tradeId);

    /**
     * Validate if a trade can be executed.
     */
    boolean canExecuteTrade(String userId, String stockSymbol, Trade.TradeType tradeType, Integer quantity);
} 