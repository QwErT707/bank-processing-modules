CREATE TABLE products_registry (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    interest_rate NUMERIC(10,4) NOT NULL,
    open_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_products_registry_client_id ON products_registry(client_id);
CREATE INDEX idx_products_registry_account_id ON products_registry(account_id);
CREATE INDEX idx_products_registry_product_id ON products_registry(product_id);
CREATE INDEX idx_products_registry_open_date ON products_registry(open_date);