package com.stocktrading.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Trade responses.
 */
public class TradeDto {

    private Long id;
    private String userId;
    private String stockSymbol;
    private String tradeType;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime timestamp;
    private String status;
    private String statusMessage;
    private BigDecimal totalValue;

    public TradeDto() {}

    public TradeDto(Long id, String userId, String stockSymbol, String tradeType,
                   Integer quantity, BigDecimal price, LocalDateTime timestamp,
                   String status, String statusMessage, BigDecimal totalValue) {
        this.id = id;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
        this.status = status;
        this.statusMessage = statusMessage;
        this.totalValue = totalValue;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
} 