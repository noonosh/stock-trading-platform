package com.stocktrading.application.service.impl;

import com.stocktrading.application.service.StockPriceService;
import com.stocktrading.domain.model.Stock;
import com.stocktrading.domain.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Mock implementation of StockPriceService for demonstration purposes.
 * This service simulates real stock price data and updates.
 * Follows Single Responsibility Principle - only handles stock price operations.
 */
@Service
@Transactional
public class MockStockPriceServiceImpl implements StockPriceService {

    private final StockRepository stockRepository;

    @Autowired
    public MockStockPriceServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        initializeMockData();
    }

    @Override
    public Optional<BigDecimal> getCurrentPrice(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .map(Stock::getCurrentPrice);
    }

    @Override
    public Optional<Stock> getStock(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase());
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAllOrderByLastUpdatedDesc();
    }

    @Override
    public List<Stock> searchStocks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllStocks();
        }
        return stockRepository.searchBySymbolOrCompanyName(searchTerm.trim());
    }

    @Override
    public void updateStockPrice(String symbol, BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        stockRepository.findBySymbol(symbol.toUpperCase())
                .ifPresent(stock -> {
                    stock.updatePrice(newPrice);
                    stockRepository.save(stock);
                });
    }

    @Override
    public Stock addStock(String symbol, String companyName, BigDecimal price) {
        if (stockExists(symbol)) {
            throw new IllegalArgumentException("Stock with symbol " + symbol + " already exists");
        }

        Stock stock = new Stock(symbol.toUpperCase(), companyName, price);
        return stockRepository.save(stock);
    }

    @Override
    public boolean stockExists(String symbol) {
        return stockRepository.existsBySymbol(symbol.toUpperCase());
    }

    /**
     * Initialize mock stock data for demonstration purposes.
     */
    private void initializeMockData() {
        if (stockRepository.count() == 0) {
            initializeStockData();
        }
    }

    private void initializeStockData() {
        List<MockStockData> mockStocks = List.of(
                new MockStockData("AAPL", "Apple Inc.", new BigDecimal("175.50")),
                new MockStockData("GOOGL", "Alphabet Inc.", new BigDecimal("142.30")),
                new MockStockData("MSFT", "Microsoft Corporation", new BigDecimal("380.90")),
                new MockStockData("AMZN", "Amazon.com Inc.", new BigDecimal("155.20")),
                new MockStockData("TSLA", "Tesla Inc.", new BigDecimal("248.75")),
                new MockStockData("META", "Meta Platforms Inc.", new BigDecimal("485.60")),
                new MockStockData("NVDA", "NVIDIA Corporation", new BigDecimal("875.30")),
                new MockStockData("NFLX", "Netflix Inc.", new BigDecimal("456.80")),
                new MockStockData("AMD", "Advanced Micro Devices", new BigDecimal("185.40")),
                new MockStockData("CRM", "Salesforce Inc.", new BigDecimal("275.90"))
        );

        mockStocks.forEach(mockStock -> {
            if (!stockExists(mockStock.symbol)) {
                Stock stock = new Stock(mockStock.symbol, mockStock.companyName, mockStock.price);
                stockRepository.save(stock);
            }
        });
    }

    private static class MockStockData {
        final String symbol;
        final String companyName;
        final BigDecimal price;

        MockStockData(String symbol, String companyName, BigDecimal price) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.price = price;
        }
    }
} 