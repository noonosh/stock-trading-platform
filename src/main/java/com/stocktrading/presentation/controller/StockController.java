package com.stocktrading.presentation.controller;

import com.stocktrading.application.service.StockPriceService;
import com.stocktrading.domain.model.Stock;
import com.stocktrading.presentation.dto.StockDto;
import com.stocktrading.presentation.dto.UpdatePriceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for stock-related operations.
 * Follows RESTful design principles and proper HTTP status codes.
 */
@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
public class StockController {

    private final StockPriceService stockPriceService;

    @Autowired
    public StockController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    /**
     * Get all available stocks.
     */
    @GetMapping
    public ResponseEntity<List<StockDto>> getAllStocks() {
        List<Stock> stocks = stockPriceService.getAllStocks();
        List<StockDto> stockDtos = stocks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stockDtos);
    }

    /**
     * Get stock by symbol.
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<StockDto> getStock(@PathVariable String symbol) {
        return stockPriceService.getStock(symbol)
                .map(stock -> ResponseEntity.ok(convertToDto(stock)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search stocks by symbol or company name.
     */
    @GetMapping("/search")
    public ResponseEntity<List<StockDto>> searchStocks(@RequestParam String query) {
        List<Stock> stocks = stockPriceService.searchStocks(query);
        List<StockDto> stockDtos = stocks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stockDtos);
    }

    /**
     * Get current price for a stock.
     */
    @GetMapping("/{symbol}/price")
    public ResponseEntity<BigDecimal> getCurrentPrice(@PathVariable String symbol) {
        return stockPriceService.getCurrentPrice(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update stock price (for mock API simulation).
     */
    @PutMapping("/{symbol}/price")
    public ResponseEntity<Void> updateStockPrice(
            @PathVariable String symbol,
            @Valid @RequestBody UpdatePriceRequest request) {
        try {
            stockPriceService.updateStockPrice(symbol, request.getNewPrice());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add a new stock (for mock API simulation).
     */
    @PostMapping
    public ResponseEntity<StockDto> addStock(@Valid @RequestBody StockDto stockDto) {
        try {
            Stock stock = stockPriceService.addStock(
                    stockDto.getSymbol(), 
                    stockDto.getCompanyName(), 
                    stockDto.getCurrentPrice()
            );
            return ResponseEntity.ok(convertToDto(stock));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Convert Stock entity to DTO.
     */
    private StockDto convertToDto(Stock stock) {
        return new StockDto(
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getCurrentPrice(),
                stock.getChangePercentage(),
                stock.getLastUpdated(),
                stock.getOpenPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume()
        );
    }
} 