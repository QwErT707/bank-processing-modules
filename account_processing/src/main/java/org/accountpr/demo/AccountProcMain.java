package org.accountpr.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication
//@EnableScheduling
//@SpringBootApplication

@SpringBootApplication(scanBasePackages = {"org.accountpr.demo", "ru.t1hwork.starter.aop"})
@EntityScan(basePackages = {"org.accountpr.demo", "ru.t1hwork.starter.aop.model"})
@EnableJpaRepositories(basePackages = {"org.accountpr.demo", "ru.t1hwork.starter.aop.repository"})
@EnableScheduling
public class AccountProcMain {
    public static void main(String[] args) {
        SpringApplication.run(AccountProcMain.class, args);}
}
