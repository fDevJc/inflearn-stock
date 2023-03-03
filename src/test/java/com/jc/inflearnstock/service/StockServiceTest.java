package com.jc.inflearnstock.service;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jc.inflearnstock.domain.Stock;
import com.jc.inflearnstock.repository.StockRepository;

@SpringBootTest
class StockServiceTest {
	@Autowired
	private StockService stockService;
	@Autowired
	private StockRepository stockRepository;

	private Stock savedStock;

	@BeforeEach
	void beforeEach() {

		savedStock = stockRepository.save(new Stock(1L, 100L));
	}

	@AfterEach
	void afterEach() {
		stockRepository.deleteAll();
	}
	
	@Test
	void 재고감소() throws Exception {
		stockService.decrease(savedStock.getId(),1L);

		Stock ret = stockRepository.findById(savedStock.getId())
			.orElseThrow();

		Assertions.assertThat(ret.getQuantity()).isEqualTo(99L);
	}
}