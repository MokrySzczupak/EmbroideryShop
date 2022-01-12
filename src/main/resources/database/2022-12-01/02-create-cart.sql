--liquibase formatted sql
--changeset nwojtowicz:9
CREATE TABLE CART_ITEM (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   product_id BIGINT NOT NULL,
   quantity INT(3) NOT NULL,
   constraint fk_products FOREIGN KEY (product_id)
       REFERENCES PRODUCT(id),
   constraint fk_users FOREIGN KEY (user_id)
       REFERENCES USER(user_id),
   user_id BIGINT NOT NULL
);
--changeset nwojtowicz:10
CREATE TABLE CART (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_name VARCHAR(60) NULL
);
--changeset nwojtowicz:11
INSERT INTO CART_ITEM VALUES ( 1, 25, 1, 2 );
INSERT INTO CART_ITEM VALUES ( 2, 20, 1, 1 );
INSERT INTO CART_ITEM VALUES ( 3, 20, 1, 2 );
