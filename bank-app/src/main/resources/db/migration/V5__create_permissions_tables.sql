CREATE TABLE permissions (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES permissions (id)
);
ALTER TABLE users
ADD COLUMN customer_id bigint;

ALTER TABLE users
ADD CONSTRAINT fk_users_customer
FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE cards
    ADD COLUMN type varchar(20) NOT NULL DEFAULT 'DEBIT';
