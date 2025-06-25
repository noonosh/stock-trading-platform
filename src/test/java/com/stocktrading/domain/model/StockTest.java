package com.stocktrading.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Stock entity.
 * Demonstrates testability and proper validation.
 */
class StockTest {

    @Test
    @DisplayName("Should create stock with valid parameters")
    void shouldCreateStockWithValidParameters() {
        // Given
        String symbol = "AAPL";
        String companyName = "Apple Inc.";
        BigDecimal price = new BigDecimal("150.00");

        // When
        Stock stock = new Stock(symbol, companyName, price);

        // Then
        assertEquals(symbol, stock.getSymbol());
        assertEquals(companyName, stock.getCompanyName());
        assertEquals(price, stock.getCurrentPrice());
        assertNotNull(stock.getLastUpdated());
    }

    @Test
    @DisplayName("Should throw exception when symbol is null")
    void shouldThrowExceptionWhenSymbolIsNull() {
        // Given
        String symbol = null;
        String companyName = "Apple Inc.";
        BigDecimal price = new BigDecimal("150.00");

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new Stock(symbol, companyName, price);
        });
    }

    @Test
    @DisplayName("Should throw exception when company name is null")
    void shouldThrowExceptionWhenCompanyNameIsNull() {
        // Given
        String symbol = "AAPL";
        String companyName = null;
        BigDecimal price = new BigDecimal("150.00");

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new Stock(symbol, companyName, price);
        });
    }

    @Test
    @DisplayName("Should throw exception when price is null")
    void shouldThrowExceptionWhenPriceIsNull() {
        // Given
        String symbol = "AAPL";
        String companyName = "Apple Inc.";
        BigDecimal price = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new Stock(symbol, companyName, price);
        });
    }

    @Test
    @DisplayName("Should update price and calculate change percentage")
    void shouldUpdatePriceAndCalculateChangePercentage() {
        // Given
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("100.00"));
        BigDecimal newPrice = new BigDecimal("110.00");

        // When
        stock.updatePrice(newPrice);

        // Then
        assertEquals(newPrice, stock.getCurrentPrice());
        assertEquals(new BigDecimal("10.0000"), stock.getChangePercentage());
        assertNotNull(stock.getLastUpdated());
    }

    @Test
    @DisplayName("Should throw exception when updating with null price")
    void shouldThrowExceptionWhenUpdatingWithNullPrice() {
        // Given
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            stock.updatePrice(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with zero price")
    void shouldThrowExceptionWhenUpdatingWithZeroPrice() {
        // Given
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            stock.updatePrice(BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with negative price")
    void shouldThrowExceptionWhenUpdatingWithNegativePrice() {
        // Given
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            stock.updatePrice(new BigDecimal("-10.00"));
        });
    }

    @Test
    @DisplayName("Should set daily data correctly")
    void shouldSetDailyDataCorrectly() {
        // Given
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        BigDecimal openPrice = new BigDecimal("145.00");
        BigDecimal highPrice = new BigDecimal("155.00");
        BigDecimal lowPrice = new BigDecimal("143.00");
        Long volume = 1000000L;

        // When
        stock.setDailyData(openPrice, highPrice, lowPrice, volume);

        // Then
        assertEquals(openPrice, stock.getOpenPrice());
        assertEquals(highPrice, stock.getHighPrice());
        assertEquals(lowPrice, stock.getLowPrice());
        assertEquals(volume, stock.getVolume());
    }

    @Test
    @DisplayName("Should implement equals correctly based on symbol")
    void shouldImplementEqualsCorrectlyBasedOnSymbol() {
        // Given
        Stock stock1 = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        Stock stock2 = new Stock("AAPL", "Apple Inc.", new BigDecimal("160.00"));
        Stock stock3 = new Stock("GOOGL", "Alphabet Inc.", new BigDecimal("150.00"));

        // When & Then
        assertEquals(stock1, stock2); // Same symbol
        assertNotEquals(stock1, stock3); // Different symbol
        assertEquals(stock1.hashCode(), stock2.hashCode()); // Same hash code
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // Given
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));

        // When
        String toString = stock.toString();

        // Then
        assertTrue(toString.contains("AAPL"));
        assertTrue(toString.contains("Apple Inc."));
        assertTrue(toString.contains("150.00"));
    }
} 