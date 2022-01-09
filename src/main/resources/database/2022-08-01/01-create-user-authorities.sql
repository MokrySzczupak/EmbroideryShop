--liquibase formatted sql
--changeset nwojtowicz:4
CREATE TABLE USER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR (80) NOT NULL UNIQUE,
    username VARCHAR (50) NOT NULL,
    password VARCHAR (80) NOT NULL
);
--changeset nwojtowicz:5
CREATE TABLE AUTHORITIES (
    email VARCHAR (50) NOT NULL,
    authority VARCHAR (50) NOT NULL,
    constraint fk_authorities_user FOREIGN KEY (email) REFERENCES
    USER(email),
    UNIQUE KEY username_authority (email, authority)
);
--changeset nwojtowicz:6
INSERT INTO USER (id, email, username, password) VALUES (1, 'test@test.test', 'test', '{bcrypt}$2a$12$O58I/RhIHdGZF96XUsovCuFX4FscEQlS132gBRQtzLbDr2lfW3HSa');
INSERT INTO AUTHORITIES (email, authority) VALUES ('test@test.test', 'USER');