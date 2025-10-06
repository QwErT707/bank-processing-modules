package ru.t1hwork.starter.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.t1hwork.starter.aop.annotations.HttpOutcomeRequestLog;
import ru.t1hwork.starter.aop.model.ErrorLog;
import ru.t1hwork.starter.aop.model.dto.ErrorLogDTO;
import ru.t1hwork.starter.aop.repository.ErrorLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;

@Aspect
//@Component сreeate through injection spring
@Slf4j
@RequiredArgsConstructor
public class HttpOutcomeRequestLogAspect {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final ErrorLogRepository errorLogRepository;
    @Value("${spring.application.name}")
    private String microserviceName;

    @AfterReturning(pointcut = "@annotation(httpOutcomeRequestLog)", returning = "result")
        public void logOutcomeRequest(JoinPoint joinPoint, HttpOutcomeRequestLog httpOutcomeRequestLog, Object result){
       try{
        String uri= extractUri(joinPoint.getArgs());
        String params=extractParams(joinPoint.getArgs());
        String body= extractBody(joinPoint.getArgs());

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
            ErrorLog errorLog=ErrorLog.builder().microserviceName(microserviceName)
                    .timestamp(errorLogDTO.getTimestamp())
                    .methodSignature(errorLogDTO.getMethodSignature())
                    .requestUri(errorLogDTO.getRequestUri())
                    .requestParams(errorLogDTO.getRequestParams())
                    .requestBody(errorLogDTO.getRequestBody())
                    .logType("INFO").build();
            errorLogRepository.save(errorLog);
        }log.info("🎯🎯🎯 HTTP OUTCOME from {}: {} {}🎯🎯🎯 ", microserviceName, uri, params);
       }catch (Exception aspectEx){
           log.error("🎯🎯🎯 Error in HttpOutcomeRequestLogAspect: {}🎯🎯🎯 ", aspectEx.getMessage());
       }
    }

       private String extractUri(Object[] args) {
        for (Object arg: args){
            if(arg instanceof String && ((String) arg).startsWith("http")){
                return (String) arg;
            }
        }return "🎯🎯🎯 Unknown URI";
    }
    private String extractBody(Object[] args) {
        return Arrays.toString(args);
    }
    private String extractParams(Object[] args) {
        for (Object arg:args){
            if (arg!=null && !(arg instanceof String)){
                return arg.toString();
            }
        }return null;
    }
}
