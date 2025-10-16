package org.clientpr.demo.config;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.clientpr.demo.model.enums.ProductKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProductMetricsConfiguration {
    @Bean
    public Counter totalProductsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("products.created.total")
                .description("Total number of all products created")
                .register(registry);
    }

    @Bean
    public Counter totalProductsClosedCounter(MeterRegistry registry) {
        return Counter.builder("products.closed.total")
                .description("Total number of all products closed")
                .register(registry);
    }

    @Bean
    public Map<ProductKey, Counter> productTypeOpenCounters(MeterRegistry registry) {
        Map<ProductKey, Counter> counters = new HashMap<>();

        Arrays.stream(ProductKey.values()).forEach(productKey -> {
            Counter counter = Counter.builder("products.opened.by.type")
                    .description("Number of opened products by type")
                    .tag("product_type", productKey.name())
                    .tag("product_name", getProductName(productKey))
                    .register(registry);
            counters.put(productKey, counter);
        });

        return counters;
    }

    @Bean
    public Map<ProductKey, Counter> productTypeCloseCounters(MeterRegistry registry) {
        Map<ProductKey, Counter> counters = new HashMap<>();

        Arrays.stream(ProductKey.values()).forEach(productKey -> {
            Counter counter = Counter.builder("products.closed.by.type")
                    .description("Number of closed products by type")
                    .tag("product_type", productKey.name())
                    .tag("product_name", getProductName(productKey))
                    .register(registry);
            counters.put(productKey, counter);
        });

        return counters;
    }

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
