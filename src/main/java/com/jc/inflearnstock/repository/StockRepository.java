package com.jc.inflearnstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jc.inflearnstock.domain.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
	
}
