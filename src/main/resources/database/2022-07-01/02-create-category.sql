--liquibase formatted sql
--changeset nwojtowicz:2
CREATE TABLE CATEGORY (
  category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

ALTER TABLE PRODUCT
    ADD FOREIGN KEY (category_id) REFERENCES CATEGORY(category_id);
