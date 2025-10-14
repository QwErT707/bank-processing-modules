package ru.t1hwork.starter.aop.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1hwork.starter.aop.aspect.*;
import ru.t1hwork.starter.aop.jwt.JwtAuthenticationFilter;
import ru.t1hwork.starter.aop.properties.AopProperties;
import ru.t1hwork.starter.aop.repository.ErrorLogRepository;
@Slf4j
@Configuration
@ConditionalOnProperty(name="t1.aop.enabled", havingValue="true", matchIfMissing = true)
@EnableConfigurationProperties(AopProperties.class)
@EnableAspectJAutoProxy
public class AopAutoConfiguration {  public AopAutoConfiguration() {
 log.info("🎯 AopAutoConfiguration LOADED! 🎯");
}

 @PostConstruct
 public void init() {
  log.info("🎯 T1 AOP Starter INITIALIZED! 🎯");
 }
   @Bean
   @ConditionalOnClass(KafkaTemplate.class)
   @ConditionalOnProperty(name="t1.aop.kafka.enabled", havingValue="true")
    public LogDatasourceErrorAspect logDatasourceErrorAspect(KafkaTemplate<String, Object> kafkaTemplate,
                                                             ErrorLogRepository errorLogRepository){
    log.info("🎯🎯🎯 Creating LogDatasourceErrorAspect bean 🎯🎯🎯");
    return new LogDatasourceErrorAspect(kafkaTemplate, errorLogRepository);}
    @Bean
    @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnProperty(name = "t1.aop.kafka.enabled", havingValue = "true")
    public HttpOutcomeRequestLogAspect httpOutcomeRequestLogAspect(KafkaTemplate<String, Object> kafkaTemplate,
                                                                   ErrorLogRepository errorLogRepository){return new HttpOutcomeRequestLogAspect(kafkaTemplate, errorLogRepository);}
    @Bean
    @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnProperty(name = "t1.aop.kafka.enabled", havingValue = "true")
    public HttpIncomeRequestLogAspect httpIncomeRequestLogAspect(KafkaTemplate<String, Object> kafkaTemplate,
                                                                 ErrorLogRepository errorLogRepository){return new HttpIncomeRequestLogAspect(kafkaTemplate, errorLogRepository);}
     @Bean
     @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnProperty(name = "t1.aop.metric.enabled", havingValue = "true")
    public MetricAspect metricAspect(KafkaTemplate<String, Object> kafkaTemplate){
    return new MetricAspect(kafkaTemplate);}
    @Bean
    @ConditionalOnProperty(name = "t1.aop.cache.enabled", havingValue = "true")
    public CachedAspect cachedAspect(){return new CachedAspect();}
//@Bean
// public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter){
// FilterRegistrationBean<JwtAuthenticationFilter>registration=new FilterRegistrationBean<>();
// registration.setFilter(filter);
// registration.addUrlPatterns("/api/*");
// registration.setOrder(1);
// return registration;
//}
}
