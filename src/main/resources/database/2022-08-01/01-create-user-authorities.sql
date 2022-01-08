--liquibase formatted sql
--changeset nwojtowicz:4
CREATE TABLE USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR (50) NOT NULL UNIQUE,
    password VARCHAR (100) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE AUTHORITIES (
    username VARCHAR (50) NOT NULL,
    authority VARCHAR (50) NOT NULL,
    constraint fk_authorities_users FOREIGN KEY (username) REFERENCES
    USERS(username),
    UNIQUE KEY username_authority (username, authority)
);

INSERT INTO USERS (id, username, password, enabled) VALUES (1, 'test', '{bcrypt}$2a$12$O58I/RhIHdGZF96XUsovCuFX4FscEQlS132gBRQtzLbDr2lfW3HSa', true);
INSERT INTO AUTHORITIES (username, authority) VALUES ('test', 'USER');