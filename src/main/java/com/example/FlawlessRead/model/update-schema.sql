CREATE TABLE books_read
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    titulo       VARCHAR(255) NULL,
    autor        VARCHAR(255) NULL,
    isbn         VARCHAR(255) NULL,
    genero       VARCHAR(255) NULL,
    capa_url     VARCHAR(255) NULL,
    publish_date date NULL,
    CONSTRAINT pk_books_read PRIMARY KEY (id)
);