CREATE TABLE IF NOT EXISTS customers
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY,
    number     VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loans
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY,
    customer_id  BIGINT,
    amount       DOUBLE  NOT NULL,
    status       VARCHAR(255),
    score        INTEGER NOT NULL,
    limit_amount DOUBLE  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

