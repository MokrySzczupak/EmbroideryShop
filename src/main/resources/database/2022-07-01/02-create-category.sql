--liquibase formatted sql
--changeset nwojtowicz:2
CREATE TABLE category (
  category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

ALTER TABLE product
    ADD FOREIGN KEY (category_id) REFERENCES category(category_id);
