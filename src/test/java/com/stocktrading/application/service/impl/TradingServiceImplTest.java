package com.stocktrading.application.service.impl;

import com.stocktrading.application.service.StockPriceService;
import com.stocktrading.application.service.PortfolioService;
import com.stocktrading.domain.model.Trade;
import com.stocktrading.domain.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TradingServiceImpl.
 * Demonstrates proper mocking and isolation of business logic.
 */
@ExtendWith(MockitoExtension.class)
class TradingServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private StockPriceService stockPriceService;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private TradingServiceImpl tradingService;

    private static final String USER_ID = "user123";
    private static final String STOCK_SYMBOL = "AAPL";
    private static final Integer QUANTITY = 10;
    private static final BigDecimal PRICE = new BigDecimal("150.00");

    @BeforeEach
    void setUp() {
        // Reset mocks for each test - no global stubbing
    }

    @Test
    @DisplayName("Should successfully execute buy order")
    void shouldSuccessfullyExecuteBuyOrder() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(stockPriceService.getCurrentPrice(STOCK_SYMBOL)).thenReturn(Optional.of(PRICE));
        
        Trade expectedTrade = new Trade(USER_ID, STOCK_SYMBOL, Trade.TradeType.BUY, QUANTITY, PRICE);
        expectedTrade.markAsExecuted();
        
        when(tradeRepository.save(any(Trade.class))).thenReturn(expectedTrade);
        doNothing().when(portfolioService).updatePortfolioAfterTrade(
                eq(USER_ID), eq(STOCK_SYMBOL), eq(QUANTITY), eq(PRICE), eq(true));

        // When
        Trade result = tradingService.buyStock(USER_ID, STOCK_SYMBOL, QUANTITY);

        // Then
        assertNotNull(result);
        assertEquals(Trade.TradeType.BUY, result.getTradeType());
        assertEquals(USER_ID, result.getUserId());
        assertEquals(STOCK_SYMBOL, result.getStockSymbol());
        assertEquals(QUANTITY, result.getQuantity());
        assertEquals(PRICE, result.getPrice());
        assertTrue(result.isExecuted());

        // Verify interactions
        verify(stockPriceService).stockExists(STOCK_SYMBOL);
        verify(stockPriceService).getCurrentPrice(STOCK_SYMBOL);
        verify(portfolioService).updatePortfolioAfterTrade(USER_ID, STOCK_SYMBOL, QUANTITY, PRICE, true);
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    @DisplayName("Should successfully execute sell order when user has sufficient shares")
    void shouldSuccessfullyExecuteSellOrder() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(stockPriceService.getCurrentPrice(STOCK_SYMBOL)).thenReturn(Optional.of(PRICE));
        when(portfolioService.hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY)).thenReturn(true);
        
        Trade expectedTrade = new Trade(USER_ID, STOCK_SYMBOL, Trade.TradeType.SELL, QUANTITY, PRICE);
        expectedTrade.markAsExecuted();
        
        when(tradeRepository.save(any(Trade.class))).thenReturn(expectedTrade);
        doNothing().when(portfolioService).updatePortfolioAfterTrade(
                eq(USER_ID), eq(STOCK_SYMBOL), eq(QUANTITY), eq(PRICE), eq(false));

        // When
        Trade result = tradingService.sellStock(USER_ID, STOCK_SYMBOL, QUANTITY);

        // Then
        assertNotNull(result);
        assertEquals(Trade.TradeType.SELL, result.getTradeType());
        assertTrue(result.isExecuted());

        // Verify interactions
        verify(portfolioService).hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY);
        verify(portfolioService).updatePortfolioAfterTrade(USER_ID, STOCK_SYMBOL, QUANTITY, PRICE, false);
    }

    @Test
    @DisplayName("Should throw exception when buying non-existent stock")
    void shouldThrowExceptionWhenBuyingNonExistentStock() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(USER_ID, STOCK_SYMBOL, QUANTITY);
        });

        verify(stockPriceService).stockExists(STOCK_SYMBOL);
        verify(stockPriceService, never()).getCurrentPrice(any());
        verify(portfolioService, never()).updatePortfolioAfterTrade(any(), any(), any(), any(), anyBoolean());
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when selling without sufficient shares")
    void shouldThrowExceptionWhenSellingWithoutSufficientShares() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(portfolioService.hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.sellStock(USER_ID, STOCK_SYMBOL, QUANTITY);
        });

        verify(portfolioService).hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY);
        verify(portfolioService, never()).updatePortfolioAfterTrade(any(), any(), any(), any(), anyBoolean());
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when price is not available")
    void shouldThrowExceptionWhenPriceIsNotAvailable() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(stockPriceService.getCurrentPrice(STOCK_SYMBOL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(USER_ID, STOCK_SYMBOL, QUANTITY);
        });

        verify(stockPriceService).getCurrentPrice(STOCK_SYMBOL);
        verify(portfolioService, never()).updatePortfolioAfterTrade(any(), any(), any(), any(), anyBoolean());
    }

    @Test
    @DisplayName("Should throw exception when user ID is null")
    void shouldThrowExceptionWhenUserIdIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(null, STOCK_SYMBOL, QUANTITY);
        });

        verify(stockPriceService, never()).stockExists(any());
    }

    @Test
    @DisplayName("Should throw exception when stock symbol is null")
    void shouldThrowExceptionWhenStockSymbolIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(USER_ID, null, QUANTITY);
        });

        verify(stockPriceService, never()).stockExists(any());
    }

    @Test
    @DisplayName("Should throw exception when quantity is null")
    void shouldThrowExceptionWhenQuantityIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(USER_ID, STOCK_SYMBOL, null);
        });

        verify(stockPriceService, never()).stockExists(any());
    }

    @Test
    @DisplayName("Should throw exception when quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(USER_ID, STOCK_SYMBOL, 0);
        });

        verify(stockPriceService, never()).stockExists(any());
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tradingService.buyStock(USER_ID, STOCK_SYMBOL, -5);
        });

        verify(stockPriceService, never()).stockExists(any());
    }

    @Test
    @DisplayName("Should return true when trade can be executed for buy order")
    void shouldReturnTrueWhenTradeCanBeExecutedForBuyOrder() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(stockPriceService.getCurrentPrice(STOCK_SYMBOL)).thenReturn(Optional.of(PRICE));
        
        // When
        boolean canExecute = tradingService.canExecuteTrade(USER_ID, STOCK_SYMBOL, Trade.TradeType.BUY, QUANTITY);

        // Then
        assertTrue(canExecute);
        verify(stockPriceService).stockExists(STOCK_SYMBOL);
        verify(stockPriceService).getCurrentPrice(STOCK_SYMBOL);
    }

    @Test
    @DisplayName("Should return true when trade can be executed for sell order with sufficient shares")
    void shouldReturnTrueWhenTradeCanBeExecutedForSellOrder() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(portfolioService.hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY)).thenReturn(true);

        // When
        boolean canExecute = tradingService.canExecuteTrade(USER_ID, STOCK_SYMBOL, Trade.TradeType.SELL, QUANTITY);

        // Then
        assertTrue(canExecute);
        verify(portfolioService).hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY);
    }

    @Test
    @DisplayName("Should return false when trade cannot be executed due to insufficient shares")
    void shouldReturnFalseWhenTradeCannotBeExecutedDueToInsufficientShares() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(true);
        when(portfolioService.hasEnoughShares(USER_ID, STOCK_SYMBOL, QUANTITY)).thenReturn(false);

        // When
        boolean canExecute = tradingService.canExecuteTrade(USER_ID, STOCK_SYMBOL, Trade.TradeType.SELL, QUANTITY);

        // Then
        assertFalse(canExecute);
    }

    @Test
    @DisplayName("Should return false when stock does not exist")
    void shouldReturnFalseWhenStockDoesNotExist() {
        // Given
        when(stockPriceService.stockExists(STOCK_SYMBOL)).thenReturn(false);

        // When
        boolean canExecute = tradingService.canExecuteTrade(USER_ID, STOCK_SYMBOL, Trade.TradeType.BUY, QUANTITY);

        // Then
        assertFalse(canExecute);
    }
} 