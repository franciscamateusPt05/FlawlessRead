

CREATE TABLE book
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    titulo       VARCHAR(255) NULL,
    autor        VARCHAR(255) NULL,
    isbn         VARCHAR(255) NULL,
    genero       VARCHAR(255) NULL,
    capa_url     VARCHAR(255) NULL,
    publish_date date NULL,
    `key`        VARCHAR(255) NULL,
    CONSTRAINT pk_book PRIMARY KEY (id)
);

CREATE TABLE user
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255)          NULL,
    email    VARCHAR(255)          NULL,
    password VARCHAR(255)          NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_already_read
(
    user_id         BIGINT NOT NULL,
    already_read_id BIGINT NOT NULL,
    CONSTRAINT pk_user_alreadyread PRIMARY KEY (user_id, already_read_id)
);

CREATE TABLE user_want_to_read
(
    user_id         BIGINT NOT NULL,
    want_to_read_id BIGINT NOT NULL,
    CONSTRAINT pk_user_wanttoread PRIMARY KEY (user_id, want_to_read_id)
);

ALTER TABLE user_already_read
    ADD CONSTRAINT fk_usealrrea_on_book FOREIGN KEY (already_read_id) REFERENCES book (id);

ALTER TABLE user_already_read
    ADD CONSTRAINT fk_usealrrea_on_user FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user_want_to_read
    ADD CONSTRAINT fk_usewantorea_on_book FOREIGN KEY (want_to_read_id) REFERENCES book (id);

ALTER TABLE user_want_to_read
    ADD CONSTRAINT fk_usewantorea_on_user FOREIGN KEY (user_id) REFERENCES user (id);