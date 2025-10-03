package org.clientpr.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.clientpr.demo", "org.aop"})
@EntityScan(basePackages = {"org.clientpr.demo", "org.aop.model"})
@EnableJpaRepositories(basePackages = {"org.clientpr.demo", "org.aop.repository"})
@EnableScheduling
//@SpringBootApplication
public class ClientProcMain {
    public static void main(String[] args) {
        SpringApplication.run(ClientProcMain.class, args);
    }
}
