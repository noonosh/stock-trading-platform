package com.stocktrading.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(nullable = false)
    @NotBlank(message = "Stock symbol is required")
    private String stockSymbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Trade type is required")
    private TradeType tradeType;

    @Column(nullable = false)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;

    @Column(length = 500)
    private String statusMessage;

    protected Trade() {
        // JPA requires default constructor
    }

    public Trade(String userId, String stockSymbol, TradeType tradeType, Integer quantity, BigDecimal price) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.stockSymbol = Objects.requireNonNull(stockSymbol, "Stock symbol cannot be null");
        this.tradeType = Objects.requireNonNull(tradeType, "Trade type cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.timestamp = LocalDateTime.now();
        this.status = TradeStatus.PENDING;
        
        validateTradeData();
    }

    private void validateTradeData() {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    public BigDecimal getTotalValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void markAsExecuted() {
        this.status = TradeStatus.EXECUTED;
        this.statusMessage = "Trade executed successfully";
    }

    public void markAsFailed(String reason) {
        this.status = TradeStatus.FAILED;
        this.statusMessage = reason;
    }

    public void markAsCancelled(String reason) {
        this.status = TradeStatus.CANCELLED;
        this.statusMessage = reason;
    }

    public boolean isExecuted() {
        return status == TradeStatus.EXECUTED;
    }

    public boolean isPending() {
        return status == TradeStatus.PENDING;
    }

    public boolean isFailed() {
        return status == TradeStatus.FAILED;
    }

    // Getters
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getStockSymbol() { return stockSymbol; }
    public TradeType getTradeType() { return tradeType; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TradeStatus getStatus() { return status; }
    public String getStatusMessage() { return statusMessage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Objects.equals(id, trade.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", tradeType=" + tradeType +
                ", quantity=" + quantity +
                ", price=" + price +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }

    public enum TradeType {
        BUY, SELL
    }

    public enum TradeStatus {
        PENDING, EXECUTED, FAILED, CANCELLED
    }
} 