CREATE TABLE bonus_accounts (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT       NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    balance      NUMERIC(19,2) NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_bonus_accounts_customer ON bonus_accounts(customer_id);


CREATE TABLE cashback_rules (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    category        VARCHAR(50),
    min_amount      NUMERIC(19,2),
    percent         NUMERIC(5,2)  NOT NULL,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_cashback_rules_active ON cashback_rules(active);


CREATE TABLE bonus_transactions (
    id              BIGSERIAL PRIMARY KEY,
    bonus_account_id BIGINT      NOT NULL REFERENCES bonus_accounts(id) ON DELETE CASCADE,
    related_tx_id   BIGINT               REFERENCES transactions(id) ON DELETE SET NULL,
    amount          NUMERIC(19,2) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_bonus_tx_account ON bonus_transactions(bonus_account_id);


CREATE TABLE notifications (
    id            BIGSERIAL PRIMARY KEY,
    customer_id   BIGINT       NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    type          VARCHAR(50)  NOT NULL,
    channel       VARCHAR(20)  NOT NULL,
    title         VARCHAR(255),
    message       TEXT,
    payload       JSONB,
    is_read       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_customer ON notifications(customer_id);
CREATE INDEX idx_notifications_read     ON notifications(is_read);


CREATE TABLE receipts (
    id            BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT      NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    content       JSONB        NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_receipts_tx ON receipts(transaction_id);


CREATE TABLE exchange_rates (
    id            BIGSERIAL PRIMARY KEY,
    base_currency   VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate          NUMERIC(19,6) NOT NULL,
    fetched_at    TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_exchange_rates_pair
    ON exchange_rates(base_currency, target_currency);
