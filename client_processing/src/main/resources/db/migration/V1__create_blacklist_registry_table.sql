CREATE TABLE blacklist_registry (
    id BIGSERIAL PRIMARY KEY,
    document_type VARCHAR(20) NOT NULL,
    document_id VARCHAR(50) NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL,
    reason TEXT,
    blacklist_expiration_date TIMESTAMP,

    CONSTRAINT uq_blacklist_document UNIQUE (document_type, document_id)
);

CREATE INDEX idx_blacklist_document_type ON blacklist_registry(document_type);
CREATE INDEX idx_blacklist_document_id ON blacklist_registry(document_id);
CREATE INDEX idx_blacklist_expiration_date ON blacklist_registry(blacklist_expiration_date);
CREATE INDEX idx_blacklist_created_at ON blacklist_registry(blacklisted_at);