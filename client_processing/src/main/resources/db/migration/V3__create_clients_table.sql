CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    client_id VARCHAR(12) NOT NULL UNIQUE CHECK (client_id ~ '^\d{12}$'),
    user_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    document_type VARCHAR(20) NOT NULL CHECK (document_type IN ('PASSPORT', 'INT_PASSPORT', 'BIRTH_CERT')),
    document_id VARCHAR(20) NOT NULL,
    document_prefix VARCHAR(10),
    document_suffix VARCHAR(10),

    CONSTRAINT fk_clients_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_clients_client_id ON clients(client_id);
CREATE INDEX idx_clients_user_id ON clients(user_id);
CREATE INDEX idx_clients_document_id ON clients(document_id);