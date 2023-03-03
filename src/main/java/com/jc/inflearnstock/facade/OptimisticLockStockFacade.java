package com.jc.inflearnstock.facade;

import org.springframework.stereotype.Service;

import com.jc.inflearnstock.service.StockService;

@Service
public class OptimisticLockStockFacade {
	private final StockService stockService;

	public OptimisticLockStockFacade(StockService stockService) {
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (true) {
			try {
				stockService.decreaseWithOptimisticLock(id, quantity);
				break;
			} catch (Exception e) {
				Thread.sleep(50);
			}
		}
	}
}
