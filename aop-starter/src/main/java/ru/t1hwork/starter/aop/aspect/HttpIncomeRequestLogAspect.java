package ru.t1hwork.starter.aop.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog;
import ru.t1hwork.starter.aop.model.ErrorLog;
import ru.t1hwork.starter.aop.model.dto.ErrorLogDTO;
import ru.t1hwork.starter.aop.repository.ErrorLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
//@Component сreeate through injection spring
@Slf4j
@RequiredArgsConstructor
public class HttpIncomeRequestLogAspect {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ErrorLogRepository errorLogRepository;
    @Value("${spring.application.name}")
    private String microserviceName;

    @Before("@annotation(httpIncomeRequestLog)")
    public void logHttpIncomeRequest(JoinPoint joinPoint, HttpIncomeRequestLog httpIncomeRequestLog){
        try{
            HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String uri=request.getRequestURI();
            String params=request.getParameterMap().entrySet().stream()
                    .map(entry->entry.getKey()+"="+ Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining(","));
            String body=extractBody(joinPoint.getArgs());
            ErrorLogDTO errorLogDTO= ErrorLogDTO.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .methodSignature(joinPoint.getSignature().toShortString())
                    .requestUri(uri)
                    .requestParams(params)
                    .requestBody(body)
                    .build();
            try{
                var message= MessageBuilder
                        .withPayload(errorLogDTO)
                        .setHeader(KafkaHeaders.TOPIC, "service_logs")
                        .setHeader(KafkaHeaders.KEY, microserviceName)
                        .setHeader("type", "INFO")
                        .build();
                        kafkaTemplate.send(message);
            }catch (Exception kafkaEx){
                ErrorLog errorLog=ErrorLog.builder()  .microserviceName(microserviceName)
                        .timestamp(errorLogDTO.getTimestamp())
                        .methodSignature(errorLogDTO.getMethodSignature())
                        .requestUri(errorLogDTO.getRequestUri())
                        .requestParams(errorLogDTO.getRequestParams())
                        .requestBody(errorLogDTO.getRequestBody())
                        .logType("INFO").build();
                errorLogRepository.save(errorLog);
            }
            log.info("🎯🎯🎯 HTTP INCOME to {}: {} {}🎯🎯🎯 ", microserviceName, uri, params);
        }catch(Exception aspectEx){
            log.error("🎯🎯🎯 Error in HttpIncomeRequestLogAspect: {}🎯🎯🎯 ", aspectEx.getMessage());
        }
    }

    private String extractBody(Object[] args) {
        for (Object arg:args){
            if(arg!=null && !(arg instanceof String)){
                return arg.toString();
            }
        }return null;
    }

}
