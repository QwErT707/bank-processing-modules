package org.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aop.annotations.Metric;
import org.aop.model.dto.ErrorLogDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${aop.metric.execution-time-limit:5000}")
    private long executionTimeLimit;
    @Value("${spring.application.name}")
    private String microserviceName;
    @Around("@annotation(metric)")
public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metric)throws Throwable{

        long startTime=System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        }finally {
            long executionTime=System.currentTimeMillis()-startTime;
            String methodName=metric.name().isEmpty()
                    ? joinPoint.getSignature().toShortString() : metric.name();

            log.debug("Method {} executed in {} ms", methodName, executionTime);
        if(executionTime>executionTimeLimit){
            sendSlowMethodWarning(joinPoint, methodName, executionTime);
        }
        }

    }

    private void sendSlowMethodWarning(ProceedingJoinPoint joinPoint, String methodName, long executionTime) {
   try{
       ErrorLogDTO errorLogDTO= ErrorLogDTO.builder()
               .timestamp(new Timestamp(System.currentTimeMillis()))
               .methodSignature(methodName)
               .methodParameters(Arrays.toString(joinPoint.getArgs()))
               .exceptionMessage("Method execution time exceeded limit: " + executionTime + " ms")
               .build();
       var message= MessageBuilder.withPayload(errorLogDTO)
               .setHeader(KafkaHeaders.TOPIC, "service_logs")
               .setHeader(KafkaHeaders.KEY, microserviceName)
               .setHeader("type", "WARNING")
               .build();
       kafkaTemplate.send(message);
   }catch (Exception e){
       log.error("Failed to send metric warning to Kafka: {}", e.getMessage());
   }

    }
}
