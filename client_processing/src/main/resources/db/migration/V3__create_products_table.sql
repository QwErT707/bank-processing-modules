CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    key VARCHAR(10) NOT NULL CHECK (key IN ('DC', 'CC', 'AC', 'IPO', 'PC', 'PENS', 'NS', 'INS', 'BS')),
    create_date TIMESTAMP NOT NULL,
    product_id VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX idx_products_key ON products(key);
CREATE INDEX idx_products_product_id ON products(product_id);