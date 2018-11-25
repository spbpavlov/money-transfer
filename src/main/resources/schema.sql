CREATE SEQUENCE account_seq;
CREATE SEQUENCE transfer_seq;

CREATE TABLE account (
  id         BIGINT   DEFAULT account_seq.nextval PRIMARY KEY,
  customerId BIGINT   NOT NULL,
  currency   CHAR(3)  NOT NULL,
  balance    BIGINT   NOT NULL,
  active     BOOLEAN  NOT NULL
);

CREATE TABLE transfer (
  id                  BIGINT      DEFAULT transfer_seq.nextval PRIMARY KEY,
  executedTimestamp   TIMESTAMP   NOT NULL,
  withdrawalAccountId BIGINT      NOT NULL,
  withdrawalAmount    BIGINT      NOT NULL,
  depositAccountId    BIGINT      NOT NULL,
  depositAmount       BIGINT      NOT NULL
);

CREATE INDEX customer_idx
  ON account (customerId);

CREATE INDEX withdraw_idx
  ON transfer (withdrawalAccountId, executedTimestamp);

CREATE INDEX deposit_idx
  ON transfer (depositAccountId, executedTimestamp);

INSERT INTO account (customerId, currency, balance, active) VALUES (1, 'USD', 100000, TRUE);
INSERT INTO account (customerId, currency, balance, active) VALUES (1, 'RUB', 500000, TRUE);
INSERT INTO account (customerId, currency, balance, active) VALUES (1, 'BTC', 700000, TRUE);