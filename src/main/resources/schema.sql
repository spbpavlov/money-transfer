CREATE SEQUENCE account_seq;

CREATE TABLE account (
  id         BIGINT   DEFAULT account_seq.nextval PRIMARY KEY,
  customerId BIGINT   NOT NULL,
  currency   CHAR(3)  NOT NULL,
  balance    BIGINT   NOT NULL,
  active     BOOLEAN  NOT NULL
);

CREATE INDEX customer_idx
  ON account (customerId);

INSERT INTO account (customerId, currency, balance, active) VALUES (1, 'USD', 12345, TRUE);