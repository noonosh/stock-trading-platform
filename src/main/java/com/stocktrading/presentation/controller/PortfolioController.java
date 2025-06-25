package com.stocktrading.presentation.controller;

import com.stocktrading.application.service.PortfolioService;
import com.stocktrading.application.service.StockPriceService;
import com.stocktrading.domain.model.Portfolio;
import com.stocktrading.presentation.dto.PortfolioDto;
import com.stocktrading.presentation.dto.PortfolioSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for portfolio operations.
 */
@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "http://localhost:3000")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final StockPriceService stockPriceService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService,
                              StockPriceService stockPriceService) {
        this.portfolioService = portfolioService;
        this.stockPriceService = stockPriceService;
    }

    /**
     * Get user's complete portfolio.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioDto>> getUserPortfolio(@PathVariable String userId) {
        try {
            List<Portfolio> portfolio = portfolioService.getUserPortfolio(userId);
            List<PortfolioDto> portfolioDtos = portfolio.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(portfolioDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user's holdings for a specific stock.
     */
    @GetMapping("/user/{userId}/stock/{symbol}")
    public ResponseEntity<PortfolioDto> getUserStockHolding(
            @PathVariable String userId,
            @PathVariable String symbol) {
        try {
            return portfolioService.getUserStockHolding(userId, symbol)
                    .map(portfolio -> ResponseEntity.ok(convertToDto(portfolio)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get portfolio summary with total values and performance.
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<PortfolioSummaryDto> getPortfolioSummary(@PathVariable String userId) {
        try {
            PortfolioService.PortfolioSummary summary = portfolioService.getPortfolioSummary(userId);
            PortfolioSummaryDto summaryDto = new PortfolioSummaryDto(
                    summary.getTotalValue(),
                    summary.getTotalCost(),
                    summary.getTotalGainLoss(),
                    summary.getTotalGainLossPercentage(),
                    summary.getTotalPositions()
            );
            return ResponseEntity.ok(summaryDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if user has enough shares to sell.
     */
    @GetMapping("/user/{userId}/stock/{symbol}/shares/{quantity}/check")
    public ResponseEntity<Boolean> checkSufficientShares(
            @PathVariable String userId,
            @PathVariable String symbol,
            @PathVariable Integer quantity) {
        try {
            boolean hasEnough = portfolioService.hasEnoughShares(userId, symbol, quantity);
            return ResponseEntity.ok(hasEnough);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Convert Portfolio entity to DTO with current market values.
     */
    private PortfolioDto convertToDto(Portfolio portfolio) {
        BigDecimal currentPrice = stockPriceService.getCurrentPrice(portfolio.getStockSymbol())
                .orElse(BigDecimal.ZERO);
        
        return new PortfolioDto(
                portfolio.getId(),
                portfolio.getUserId(),
                portfolio.getStockSymbol(),
                portfolio.getQuantity(),
                portfolio.getAveragePurchasePrice(),
                currentPrice,
                portfolio.getTotalValue(currentPrice),
                portfolio.getTotalCost(),
                portfolio.getGainLoss(currentPrice),
                portfolio.getGainLossPercentage(currentPrice),
                portfolio.getLastUpdated()
        );
    }
} 