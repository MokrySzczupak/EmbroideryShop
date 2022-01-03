CREATE TABLE PRODUCT (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100)  NOT NULL,
    description VARCHAR(2000) NULL,
    price       DECIMAL(4, 2) NOT NULL
);