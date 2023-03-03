package com.jc.inflearnstock.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jc.inflearnstock.domain.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Stock s WHERE s.id=:id")
	Optional<Stock> findByIdWithPessimisticLock(@Param("id") Long id);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT s FROM Stock s where s.id=:id")
	Optional<Stock> findByIdWithOptimisticLock(@Param("id") Long id);
}
