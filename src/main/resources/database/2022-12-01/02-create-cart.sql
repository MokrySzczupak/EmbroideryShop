--liquibase formatted sql
--changeset nwojtowicz:9
CREATE TABLE CART (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  total_price DECIMAL NOT NULL,
  constraint fk_cart_user FOREIGN KEY (user_id)
      REFERENCES USER(user_id)
);
--changeset nwojtowicz:10
CREATE TABLE CART_ITEM (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   product_id BIGINT NOT NULL,
   quantity INT(3) NOT NULL,
   constraint fk_products FOREIGN KEY (product_id)
       REFERENCES PRODUCT(id),
   constraint fk_users FOREIGN KEY (user_id)
       REFERENCES USER(user_id),
   user_id BIGINT NOT NULL,
   cart_id BIGINT NULL,
   constraint fk_cart FOREIGN KEY (cart_id)
       REFERENCES CART(id),
    sold BOOLEAN NOT NULL
);
--changeset nwojtowicz:11
INSERT INTO CART_ITEM VALUES ( 1, 25, 1, 2, NULL, false );
INSERT INTO CART_ITEM VALUES ( 2, 20, 1, 1, NULL, false );
INSERT INTO CART_ITEM VALUES ( 3, 20, 1, 2, NULL, false );
