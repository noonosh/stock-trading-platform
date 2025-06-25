package com.stocktrading.domain.repository;

import com.stocktrading.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    @Query("SELECT s FROM Stock s WHERE s.symbol IN :symbols")
    List<Stock> findBySymbolIn(List<String> symbols);

    @Query("SELECT s FROM Stock s ORDER BY s.lastUpdated DESC")
    List<Stock> findAllOrderByLastUpdatedDesc();

    @Query("SELECT s FROM Stock s WHERE s.companyName ILIKE %:searchTerm% OR s.symbol ILIKE %:searchTerm%")
    List<Stock> searchBySymbolOrCompanyName(String searchTerm);
} 