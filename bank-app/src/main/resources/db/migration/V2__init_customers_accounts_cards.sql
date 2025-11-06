CREATE TABLE customers (
    id          BIGSERIAL PRIMARY KEY,
    full_name   VARCHAR(255) NOT NULL,
    phone       VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_customers_phone ON customers(phone);

CREATE TABLE accounts (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT       NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    currency    VARCHAR(3)   NOT NULL,
    balance     NUMERIC(19,2) NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_accounts_customer ON accounts(customer_id);

CREATE TABLE cards (
    id           BIGSERIAL PRIMARY KEY,
    account_id   BIGINT       NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    card_number  VARCHAR(16)  NOT NULL UNIQUE,
    expiry_month INT          NOT NULL,
    expiry_year  INT          NOT NULL,
    cvv          VARCHAR(3)   NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_cards_account ON cards(account_id);