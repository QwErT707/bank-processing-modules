package ru.t1hwork.starter.aop.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.t1hwork.starter.aop.annotations.Metric;
import ru.t1hwork.starter.aop.annotations.Cached;


@ConfigurationProperties(prefix="t1.aop")
@Data
public class AopProperties {
    private boolean enabled=true;
    private Kafka kafka= new Kafka();
    private Metric metric= new Metric();
    private Cache cache= new Cache();

    @Data
    public static class Kafka{
        private boolean enabled=true;
        private String topic="service_logs";
    }
    @Data
    public static class Metric{
        private boolean enabled=true;
        private long executionTimeLimit=5000;
    }   @Data
    public static class Cache{
        private boolean enabled=true;
        private long defaultTtl=300000;
    }

}
