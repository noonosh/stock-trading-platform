package com.stocktrading.presentation.controller;

import com.stocktrading.application.service.TradingService;
import com.stocktrading.domain.model.Trade;
import com.stocktrading.presentation.dto.TradeDto;
import com.stocktrading.presentation.dto.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for trading operations.
 */
@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "http://localhost:3000")
public class TradingController {

    private final TradingService tradingService;

    @Autowired
    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    /**
     * Execute a buy order.
     */
    @PostMapping("/buy")
    public ResponseEntity<TradeDto> buyStock(@Valid @RequestBody TradeRequest request) {
        try {
            Trade trade = tradingService.buyStock(
                    request.getUserId(),
                    request.getStockSymbol(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(convertToDto(trade));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Execute a sell order.
     */
    @PostMapping("/sell")
    public ResponseEntity<TradeDto> sellStock(@Valid @RequestBody TradeRequest request) {
        try {
            Trade trade = tradingService.sellStock(
                    request.getUserId(),
                    request.getStockSymbol(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(convertToDto(trade));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all trades for a user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TradeDto>> getUserTrades(@PathVariable String userId) {
        try {
            List<Trade> trades = tradingService.getUserTrades(userId);
            List<TradeDto> tradeDtos = trades.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tradeDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get trades for a specific user and stock.
     */
    @GetMapping("/user/{userId}/stock/{symbol}")
    public ResponseEntity<List<TradeDto>> getUserStockTrades(
            @PathVariable String userId,
            @PathVariable String symbol) {
        try {
            List<Trade> trades = tradingService.getUserStockTrades(userId, symbol);
            List<TradeDto> tradeDtos = trades.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tradeDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get a specific trade by ID.
     */
    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeDto> getTrade(@PathVariable Long tradeId) {
        try {
            Trade trade = tradingService.getTrade(tradeId);
            return ResponseEntity.ok(convertToDto(trade));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cancel a trade.
     */
    @PutMapping("/{tradeId}/cancel")
    public ResponseEntity<Void> cancelTrade(
            @PathVariable Long tradeId,
            @RequestParam String userId) {
        try {
            boolean cancelled = tradingService.cancelTrade(tradeId, userId);
            if (cancelled) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if a trade can be executed.
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateTrade(@Valid @RequestBody TradeRequest request) {
        try {
            Trade.TradeType tradeType = "BUY".equals(request.getTradeType()) 
                    ? Trade.TradeType.BUY 
                    : Trade.TradeType.SELL;
                    
            boolean canExecute = tradingService.canExecuteTrade(
                    request.getUserId(),
                    request.getStockSymbol(),
                    tradeType,
                    request.getQuantity()
            );
            return ResponseEntity.ok(canExecute);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Convert Trade entity to DTO.
     */
    private TradeDto convertToDto(Trade trade) {
        return new TradeDto(
                trade.getId(),
                trade.getUserId(),
                trade.getStockSymbol(),
                trade.getTradeType().name(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTimestamp(),
                trade.getStatus().name(),
                trade.getStatusMessage(),
                trade.getTotalValue()
        );
    }
} 