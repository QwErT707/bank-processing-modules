package ru.t1hwork.starter.aop.aspect;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.t1hwork.starter.aop.annotations.LogDatasourceError;
import ru.t1hwork.starter.aop.model.ErrorLog;
import ru.t1hwork.starter.aop.model.dto.ErrorLogDTO;
import ru.t1hwork.starter.aop.repository.ErrorLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
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
//@Component сreeate through injection spring
@RequiredArgsConstructor
public class LogDatasourceErrorAspect {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ErrorLogRepository errorLogRepository;

    @PostConstruct
    public void init() {
        log.info("🎯🎯🎯 LogDatasourceErrorAspect @PostConstruct INITIALIZED! 🎯🎯🎯");
    }
    @Value("${spring.application.name}")
    private String microserviceName;
    @AfterThrowing(pointcut = "@annotation(logAnnotation)", throwing = "ex")
    public void logDatasourceError(JoinPoint joinPoint, LogDatasourceError logAnnotation, Exception ex) {
        log.info("=== LogDatasourceErrorAspect INITIALIZED ===");
        try {
            ErrorLogDTO errorLogDTO = createErrorLogDTO(joinPoint, ex, null, null, null);
            try {
                var message = MessageBuilder
                        .withPayload(errorLogDTO)
                        .setHeader(KafkaHeaders.TOPIC, "service_logs")
                        .setHeader(KafkaHeaders.KEY, microserviceName)
                        .setHeader("type", logAnnotation.type())
                        .build();
                kafkaTemplate.send(message);
                log.info("🎯🎯🎯 Error log sent to Kafka for method: {}🎯🎯🎯 ", joinPoint.getSignature());
            } catch (Exception kafkaEx) {
                ErrorLog errorLog = createErrorLog(errorLogDTO, logAnnotation.type());
           errorLogRepository.save(errorLog);
                log.warn("🎯🎯🎯 Kafka unavailable, error saved to DB: {}🎯🎯🎯 ", errorLog.getId());
            }
            log.error("🎯🎯🎯 DATASOURCE ERROR in {} - {}: {}🎯🎯🎯 ",
                    microserviceName, joinPoint.getSignature(), ex.getMessage());
        }catch (Exception aspectEx){
            log.error("🎯🎯🎯 Error in LogDatasourceErrorAspect: {}🎯🎯🎯 ", aspectEx.getMessage());
        }
    }

       private ErrorLogDTO createErrorLogDTO(JoinPoint joinPoint, Exception ex, String uri, String params, String body) {
    return ErrorLogDTO.builder()
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .methodSignature(joinPoint.getSignature().toShortString())
            .exceptionStackTrace(Arrays.toString(ex.getStackTrace()))
            .exceptionMessage(ex.getMessage())
            .methodParameters(Arrays.toString(joinPoint.getArgs()))
            .requestUri(uri)
            .requestParams(params)
            .requestBody(body)
            .build();
    }
    private ErrorLog createErrorLog(ErrorLogDTO errorLogDTO, String logType) {
    return ErrorLog.builder()
            .microserviceName(microserviceName)
            .timestamp(errorLogDTO.getTimestamp())
            .methodSignature(errorLogDTO.getMethodSignature())
            .exceptionStackTrace(errorLogDTO.getExceptionStackTrace())
            .exceptionMessage(errorLogDTO.getExceptionMessage())
            .methodParameters(errorLogDTO.getMethodParameters())
            .requestUri(errorLogDTO.getRequestUri())
            .requestParams(errorLogDTO.getRequestParams())
            .requestBody(errorLogDTO.getRequestBody())
            .logType(logType)
            .build();
    }

}
