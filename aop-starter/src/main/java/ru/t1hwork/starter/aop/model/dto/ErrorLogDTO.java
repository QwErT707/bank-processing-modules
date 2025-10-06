package ru.t1hwork.starter.aop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogDTO {
    private Timestamp timestamp;
    private String methodSignature;
    private String exceptionStackTrace;
    private String exceptionMessage;
    private String methodParameters;
    private String requestUri;
    private String requestParams;
    private String requestBody;
}
