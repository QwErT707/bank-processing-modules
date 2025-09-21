CREATE TABLE payments_registry (
    id BIGSERIAL PRIMARY KEY,
    product_registry_id BIGINT NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    interest_rate_amount NUMERIC(15,2) NOT NULL,
    debt_amount NUMERIC(15,2) NOT NULL,
    expired BOOLEAN NOT NULL DEFAULT false,
    payment_expiration_date TIMESTAMP
);
ALTER TABLE payments_registry
ADD CONSTRAINT fk_payments_registry_products_registry
FOREIGN KEY (product_registry_id)
REFERENCES products_registry(id)
ON DELETE CASCADE;
CREATE INDEX idx_payments_registry_product_id ON payments_registry(product_registry_id);
CREATE INDEX idx_payments_registry_payment_date ON payments_registry(payment_date);
CREATE INDEX idx_payments_registry_expired ON payments_registry(expired);
CREATE INDEX idx_payments_registry_expiration_date ON payments_registry(payment_expiration_date);
