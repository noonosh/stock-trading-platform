package com.stocktrading.domain.repository;

import com.stocktrading.domain.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByUserId(String userId);

    List<Trade> findByUserIdOrderByTimestampDesc(String userId);

    List<Trade> findByStockSymbol(String stockSymbol);

    List<Trade> findByStatus(Trade.TradeStatus status);

    @Query("SELECT t FROM Trade t WHERE t.userId = :userId AND t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC")
    List<Trade> findByUserIdAndTimestampBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Trade t WHERE t.userId = :userId AND t.stockSymbol = :stockSymbol ORDER BY t.timestamp DESC")
    List<Trade> findByUserIdAndStockSymbol(String userId, String stockSymbol);
} 