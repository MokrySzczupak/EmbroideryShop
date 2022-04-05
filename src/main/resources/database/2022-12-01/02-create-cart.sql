--liquibase formatted sql
--changeset nwojtowicz:9
CREATE TABLE cart (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  total_price DECIMAL NOT NULL,
  completed BOOLEAN NOT NULL,
  paid BOOLEAN NOT NULL,
  client_secret VARCHAR(100) NULL,
  constraint fk_cart_user FOREIGN KEY (user_id)
      REFERENCES user(user_id)
);
--changeset nwojtowicz:10
CREATE TABLE cart_item (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   product_id BIGINT NOT NULL,
   quantity INT(3) NOT NULL,
   constraint fk_products FOREIGN KEY (product_id)
       REFERENCES product(id),
   constraint fk_users FOREIGN KEY (user_id)
       REFERENCES user(user_id),
   user_id BIGINT NOT NULL,
   cart_id BIGINT NULL,
   constraint fk_cart FOREIGN KEY (cart_id)
       REFERENCES cart(id),
    sold BOOLEAN NOT NULL
);
--changeset nwojtowicz:11
INSERT INTO cart_item VALUES ( 1, 1, 1, 2, NULL, false );
INSERT INTO cart_item VALUES ( 2, 2, 1, 1, NULL, false );
INSERT INTO cart_item VALUES ( 3, 3, 1, 2, NULL, false );
