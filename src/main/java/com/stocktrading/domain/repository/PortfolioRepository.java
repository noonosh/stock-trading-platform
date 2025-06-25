package com.stocktrading.domain.repository;

import com.stocktrading.domain.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUserId(String userId);

    Optional<Portfolio> findByUserIdAndStockSymbol(String userId, String stockSymbol);

    boolean existsByUserIdAndStockSymbol(String userId, String stockSymbol);

    void deleteByUserIdAndStockSymbolAndQuantity(String userId, String stockSymbol, Integer quantity);
} 