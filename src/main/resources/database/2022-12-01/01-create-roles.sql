--liquibase formatted sql
--changeset nwojtowicz:6
CREATE TABLE ROLE (
     role_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(45) NOT NULL
);
--changeset nwojtowicz:7
CREATE TABLE USERS_ROLES (
   user_id BIGINT NOT NULL,
   role_id BIGINT NOT NULL,
   CONSTRAINT role_fk FOREIGN KEY (role_id) REFERENCES ROLE (role_id),
   CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES USER (user_id)
);
--changeset nwojtowicz:8
INSERT INTO ROLE (name) VALUES ('USER');
INSERT INTO ROLE (name) VALUES ('ADMIN');
INSERT INTO USERS_ROLES (user_id, role_id) VALUES (1, 1); -- user test has role USER
INSERT INTO USERS_ROLES (user_id, role_id) VALUES (2, 1); -- user patrick has role USER
INSERT INTO USERS_ROLES (user_id, role_id) VALUES (3, 1); -- user alex has role USER
INSERT INTO USERS_ROLES (user_id, role_id) VALUES (4, 2); -- user admin has role ADMIN