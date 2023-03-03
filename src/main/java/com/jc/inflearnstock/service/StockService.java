package com.jc.inflearnstock.service;

import org.springframework.stereotype.Service;

import com.jc.inflearnstock.domain.Stock;
import com.jc.inflearnstock.repository.StockRepository;

@Service
public class StockService {
	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	public void decrease(Long id, Long quantity) {
		//재고 조회
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		//재고 감소
		stock.decrease(quantity);
		//저장
		stockRepository.saveAndFlush(stock);
	}
}
