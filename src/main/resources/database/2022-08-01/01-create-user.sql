--liquibase formatted sql
--changeset nwojtowicz:4
CREATE TABLE USER (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR (80) NOT NULL UNIQUE,
    username VARCHAR (50) NOT NULL,
    password VARCHAR (80) NOT NULL
);
--changeset nwojtowicz:5
INSERT INTO USER (user_id, email, username, password) VALUES (1, 'test@gmail.com', 'test', '$2a$12$O58I/RhIHdGZF96XUsovCuFX4FscEQlS132gBRQtzLbDr2lfW3HSa');
INSERT INTO USER (user_id, email, username, password) VALUES (2, 'patrick@gmail.com', 'patrick', '$2a$12$O58I/RhIHdGZF96XUsovCuFX4FscEQlS132gBRQtzLbDr2lfW3HSa');
INSERT INTO USER (user_id, email, username, password) VALUES (3, 'alex@gmail.com', 'alex', '$2a$12$O58I/RhIHdGZF96XUsovCuFX4FscEQlS132gBRQtzLbDr2lfW3HSa');
INSERT INTO USER (user_id, email, username, password) VALUES (4, 'admin@gmail.com', 'admin', '$2a$12$O58I/RhIHdGZF96XUsovCuFX4FscEQlS132gBRQtzLbDr2lfW3HSa');