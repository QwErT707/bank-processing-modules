CREATE TABLE error_log (
    id BIGSERIAL PRIMARY KEY,
    microservice_name VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    method_signature TEXT NOT NULL,
    exception_stack_trace TEXT,
    exception_message TEXT,
    method_parameters TEXT,
    request_uri TEXT,
    request_params TEXT,
    request_body TEXT,
    log_type VARCHAR(20) NOT NULL CHECK (log_type IN ('ERROR', 'WARNING', 'INFO')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_error_log_timestamp ON error_log(timestamp);
CREATE INDEX idx_error_log_microservice ON error_log(microservice_name);
CREATE INDEX idx_error_log_type ON error_log(log_type);