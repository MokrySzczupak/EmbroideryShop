--liquibase formatted sql
--changeset nwojtowicz:1
CREATE TABLE product (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(100)  NOT NULL,
     description VARCHAR(2000) NULL,
     price DECIMAL(7, 2) NOT NULL,
     category_id BIGINT NOT NULL,
     main_image_name VARCHAR(50) NOT NULL
);
