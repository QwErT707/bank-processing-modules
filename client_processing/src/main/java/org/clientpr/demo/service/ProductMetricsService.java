package org.clientpr.demo.service;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clientpr.demo.model.enums.ProductKey;
import org.springframework.stereotype.Service;

import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductMetricsService {
    private final Counter totalProductsCreatedCounter;
    private final Counter totalProductsClosedCounter;
    private final Map<ProductKey, Counter> productTypeOpenCounters;
    private final Map<ProductKey, Counter> productTypeCloseCounters;

    public void recordProductOpened(ProductKey productKey) {
        totalProductsCreatedCounter.increment();

        Counter typeCounter = productTypeOpenCounters.get(productKey);
        if (typeCounter != null) {
            typeCounter.increment();
            log.info("📊 METRIC: Product OPENED - Type: {}, Name: {}",
                    productKey, getProductName(productKey));        }
    }

    public void recordProductClosed(ProductKey productKey) {
        totalProductsClosedCounter.increment();

        Counter typeCounter = productTypeCloseCounters.get(productKey);
        if (typeCounter != null) {
            typeCounter.increment();
            log.info("📊 METRIC: Product CLOSED - Type: {}, Name: {}",
                    productKey, getProductName(productKey));        }
    }

    public void recordProductCreated(ProductKey productKey) {
        totalProductsCreatedCounter.increment();
        log.info("📊 METRIC: Product CREATED - Type: {}, Name: {}",
                productKey, getProductName(productKey));    }

    private String getProductName(ProductKey productKey) {
        switch (productKey) {
            case DC: return "Дебетовая карта";
            case CC: return "Кредитная карта";
            case AC: return "Автокредит";
            case IPO: return "Ипотека";
            case PENS: return "Пенсионный продукт";
            case NS: return "Накопительный счет";
            case INS: return "Страхование";
            case BS: return "Брокерский счет";
            case PC: return "Потребительский кредит";
            default: return productKey.name();
        }
    }
}
