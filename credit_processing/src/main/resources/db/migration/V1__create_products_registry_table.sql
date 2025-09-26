CREATE TABLE products_registry (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    interest_rate NUMERIC(10,4) NOT NULL,
    month_count INTEGER NOT NULL DEFAULT 12,
    open_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_products_registry_client_id ON products_registry(client_id);
CREATE INDEX idx_products_registry_account_id ON products_registry(account_id);
CREATE INDEX idx_products_registry_product_id ON products_registry(product_id);
CREATE INDEX idx_products_registry_open_date ON products_registry(open_date);