CREATE SEQUENCE hibernate_sequence START 1 INCREMENT 1;

CREATE TABLE classes
(
    id   BIGINT NOT NULL,
    code VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE sections
(
    id   BIGINT NOT NULL,
    codes text[],
    name VARCHAR(255),
    PRIMARY KEY (id)
);

INSERT INTO SECTIONS  VALUES  (1, ARRAY['GC11', 'GC12'], 'section 1');
INSERT INTO SECTIONS  VALUES  (2, ARRAY['GC21', 'GC22'], 'section 2');