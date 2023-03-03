package com.jc.inflearnstock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jc.inflearnstock.domain.Stock;
import com.jc.inflearnstock.repository.StockRepository;

@Service
public class StockService {
	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public synchronized void decreaseWithSynchronized(Long id, Long quantity) {
		//재고 조회
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		//재고 감소
		stock.decrease(quantity);
		//저장
		stockRepository.saveAndFlush(stock);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(Long id, Long quantity) {
		//재고 조회
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		//재고 감소
		stock.decrease(quantity);
		//저장
		stockRepository.saveAndFlush(stock);
	}

	@Transactional
	public void decreaseWithPessimisticLock(Long id, Long quantity) {
		//재고 조회
		Stock stock = stockRepository.findByIdWithPessimisticLock(id).get();
		//재고 감소
		stock.decrease(quantity);
		//저장
		stockRepository.saveAndFlush(stock);
	}

	@Transactional
	public void decreaseWithOptimisticLock(Long id, Long quantity) {
		//재고 조회
		Stock stock = stockRepository.findByIdWithOptimisticLock(id).get();
		//재고 감소
		stock.decrease(quantity);
		//저장
		stockRepository.saveAndFlush(stock);
	}
}
