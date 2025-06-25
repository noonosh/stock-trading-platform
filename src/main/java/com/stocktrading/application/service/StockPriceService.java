package com.stocktrading.application.service;

import com.stocktrading.domain.model.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing stock price data.
 * Follows the Interface Segregation Principle by defining a focused contract.
 */
public interface StockPriceService {

    /**
     * Get current stock price for a given symbol.
     */
    Optional<BigDecimal> getCurrentPrice(String symbol);

    /**
     * Get stock information by symbol.
     */
    Optional<Stock> getStock(String symbol);

    /**
     * Get all available stocks.
     */
    List<Stock> getAllStocks();

    /**
     * Search stocks by symbol or company name.
     */
    List<Stock> searchStocks(String searchTerm);

    /**
     * Update stock price (for mock API simulation).
     */
    void updateStockPrice(String symbol, BigDecimal newPrice);

    /**
     * Add a new stock to the system.
     */
    Stock addStock(String symbol, String companyName, BigDecimal price);

    /**
     * Check if a stock exists in the system.
     */
    boolean stockExists(String symbol);
} 