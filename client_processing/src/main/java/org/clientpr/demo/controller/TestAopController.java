package org.clientpr.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAopController {
    @GetMapping("/test-aop")
    @ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog
    public String testAop() {
        return "AOP Test";
    }

    @GetMapping("/test-error")
    @ru.t1hwork.starter.aop.annotations.LogDatasourceError
    public String testError() {
        throw new RuntimeException("Test error for AOP");
    }
}
