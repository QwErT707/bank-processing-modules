CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    card_id VARCHAR(36) NOT NULL UNIQUE,
    payment_system VARCHAR(20) NOT NULL CHECK (payment_system IN ('VISA', 'MASTERCARD', 'MIR', 'AMEX')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'CANCELLED')),

    CONSTRAINT fk_cards_account_id FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE INDEX idx_cards_account_id ON cards(account_id);
CREATE INDEX idx_cards_payment_system ON cards(payment_system);
CREATE INDEX idx_cards_status ON cards(status);
