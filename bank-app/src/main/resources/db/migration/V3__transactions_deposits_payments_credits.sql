CREATE TABLE transactions (
    id              BIGSERIAL PRIMARY KEY,
    account_id      BIGINT       NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    related_card_id BIGINT               REFERENCES cards(id) ON DELETE SET NULL,
    type            VARCHAR(30)  NOT NULL,
    status          VARCHAR(30)  NOT NULL,
    amount          NUMERIC(19,2) NOT NULL,
    currency        VARCHAR(3)   NOT NULL,
    direction       VARCHAR(10)  NOT NULL,
    description     TEXT,
    meta            JSONB,
    suspicious      BOOLEAN      NOT NULL DEFAULT FALSE,
    suspicious_reason TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_tx_account ON transactions(account_id);
CREATE INDEX idx_tx_created_at ON transactions(created_at);
CREATE INDEX idx_tx_suspicious ON transactions(suspicious);


CREATE TABLE deposits (
    id                BIGSERIAL PRIMARY KEY,
    customer_id       BIGINT       NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    account_id        BIGINT       NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    principal_amount  NUMERIC(19,2) NOT NULL,
    currency          VARCHAR(3)   NOT NULL,
    monthly_interest  NUMERIC(5,2) NOT NULL,
    term_months       INT          NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    opened_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    closed_at         TIMESTAMPTZ
);

CREATE INDEX idx_deposits_customer ON deposits(customer_id);
CREATE INDEX idx_deposits_account  ON deposits(account_id);


CREATE TABLE payments (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT       NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    from_account_id BIGINT               REFERENCES accounts(id) ON DELETE SET NULL,
    from_card_id    BIGINT               REFERENCES cards(id) ON DELETE SET NULL,

    category        VARCHAR(50)  NOT NULL,
    provider_name   VARCHAR(255),
    details         JSONB,

    amount          NUMERIC(19,2) NOT NULL,
    currency        VARCHAR(3)    NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    scheduled_at    TIMESTAMPTZ,
    paid_at         TIMESTAMPTZ,
    transaction_id  BIGINT               REFERENCES transactions(id) ON DELETE SET NULL,

    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_payments_customer ON payments(customer_id);
CREATE INDEX idx_payments_status   ON payments(status);
CREATE INDEX idx_payments_category ON payments(category);


CREATE TABLE credits (
    id                BIGSERIAL PRIMARY KEY,
    customer_id       BIGINT       NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    principal_amount  NUMERIC(19,2) NOT NULL,
    currency          VARCHAR(3)   NOT NULL,
    interest_rate_annual NUMERIC(5,2) NOT NULL,
    term_months       INT          NOT NULL,
    credit_type       VARCHAR(50)  NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    closed_at         TIMESTAMPTZ
);

CREATE INDEX idx_credits_customer ON credits(customer_id);
CREATE INDEX idx_credits_status   ON credits(status);


CREATE TABLE installment_plans (
    id            BIGSERIAL PRIMARY KEY,
    credit_id     BIGINT       NOT NULL REFERENCES credits(id) ON DELETE CASCADE,
    installment_no INT         NOT NULL,
    due_date      DATE         NOT NULL,
    amount        NUMERIC(19,2) NOT NULL,
    paid          BOOLEAN      NOT NULL DEFAULT FALSE,
    paid_at       TIMESTAMPTZ,
    penalty_amount NUMERIC(19,2) NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX ux_installments_credit_no
    ON installment_plans(credit_id, installment_no);
