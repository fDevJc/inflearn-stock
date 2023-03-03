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

	/*
		decrease 메소드에 synchronized를 주어 다른쓰레드에서 접근하는 것을 막음
		하지만 @Transaction 애노테이션의 실행방법에 따라 원하는 결과가 이루어지지 않음
		스프링에서 Proxy를 이용하여 트랜잭션 처리를 하게 되는데 이때 트랜잭션이 종료되기전에
		다른 쓰레드에서 synchronizedDecrease를 호출할 수 있음
		(proxy 트랜잭션시작 -> 비즈니스 로직 -> 트랜잭션종료 비즈니스 로직 실행과 트랜잭션 종료 사이 시점)

		synchronized의 문제점
		동일 서버에서 실행되는 쓰레드의 경우 synchronized를 활용하여 처리가 어느정도 가능하지만
		MSA 환경에서 다른 서버에서 실행되는 쓰레드의 경우 동일하게 RACE CONDITION이 발생할 수 있다.
	 */
	@Test
	void 동시에_100개의_요청_decrease메소드_synchronized() throws Exception {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.synchronizedDecrease(1L, 1L);
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