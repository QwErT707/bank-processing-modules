CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    is_credit BOOLEAN NOT NULL,
    payed_at TIMESTAMP,
    type VARCHAR(50) NOT NULL,
    expired BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_payments_account_id FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE INDEX idx_payments_account_id ON payments(account_id);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
CREATE INDEX idx_payments_type ON payments(type);
