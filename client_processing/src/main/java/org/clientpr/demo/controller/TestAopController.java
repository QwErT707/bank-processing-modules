package org.clientpr.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAopController {
    @GetMapping("/test-aop")
    @org.aop.annotations.HttpIncomeRequestLog
    public String testAop() {
        return "AOP Test";
    }

    @GetMapping("/test-error")
    @org.aop.annotations.LogDatasourceError
    public String testError() {
        throw new RuntimeException("Test error for AOP");
    }
}
