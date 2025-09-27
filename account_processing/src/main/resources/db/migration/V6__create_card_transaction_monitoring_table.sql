CREATE TABLE card_transaction_monitoring (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_card_transaction_time ON card_transaction_monitoring(card_id, transaction_time);
CREATE INDEX idx_transaction_time ON card_transaction_monitoring(transaction_time);

COMMENT ON TABLE card_transaction_monitoring IS 'Мониторинг транзакций по картам для выявления подозрительной активности';
COMMENT ON COLUMN card_transaction_monitoring.card_id IS 'ID карты';
COMMENT ON COLUMN card_transaction_monitoring.transaction_time IS 'Время транзакции';
COMMENT ON COLUMN card_transaction_monitoring.transaction_type IS 'Тип транзакции';
COMMENT ON COLUMN card_transaction_monitoring.amount IS 'Сумма транзакции';