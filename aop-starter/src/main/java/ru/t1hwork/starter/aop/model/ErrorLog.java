package ru.t1hwork.starter.aop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name="error_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String microserviceName;

    @Column(nullable = false)
    private Timestamp timestamp;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String methodSignature;

    @Column(columnDefinition = "TEXT")
    private String exceptionStackTrace;

    @Column(columnDefinition = "TEXT")
    private String exceptionMessage;

    @Column(columnDefinition = "TEXT")
    private String methodParameters;

    @Column(columnDefinition = "TEXT")
    private String requestUri;

    @Column(columnDefinition = "TEXT")
    private String requestParams;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(nullable = false)
    private String logType;

    @Builder.Default
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
