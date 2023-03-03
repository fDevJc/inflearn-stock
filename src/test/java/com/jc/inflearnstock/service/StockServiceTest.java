package com.jc.inflearnstock.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		stockService.decrease(savedStock.getId(), 1L);

		Stock ret = stockRepository.findById(savedStock.getId())
			.orElseThrow();

		Assertions.assertThat(ret.getQuantity()).isEqualTo(99L);
	}

	@Test
	void 동시에_100개의_요청() throws Exception {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Stock stock = stockRepository.findById(savedStock.getId()).orElseThrow();

		System.out.println("stock.getQuantity() = " + stock.getQuantity());
		Assertions.assertThat(stock.getQuantity())
			.isNotEqualTo(0L);
	}
}