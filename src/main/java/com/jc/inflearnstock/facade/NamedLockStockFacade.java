package com.jc.inflearnstock.facade;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.jc.inflearnstock.repository.StockRepository;
import com.jc.inflearnstock.service.StockService;

@Component
public class NamedLockStockFacade {
	private final StockRepository stockRepository;

	private final StockService stockService;

	public NamedLockStockFacade(StockRepository stockRepository, StockService stockService) {
		this.stockRepository = stockRepository;
		this.stockService = stockService;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		try {
			stockRepository.getLock(id.toString());
			stockService.decrease(id, quantity);
		} finally {
			stockRepository.releaseLock(id.toString());
		}
	}
}
