package org.creditpr.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.creditpr.demo", "ru.t1hwork.starter.aop"})
@EntityScan(basePackages = {"org.creditpr.demo", "ru.t1hwork.starter.aop.model"})
@EnableJpaRepositories(basePackages = {"org.creditpr.demo", "ru.t1hwork.starter.aop.repository"})
@EnableScheduling
//@SpringBootApplication
public class CreditProcMain {
    public static void main(String[] args) {
        SpringApplication.run(CreditProcMain.class, args);    }
}
