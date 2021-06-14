CREATE SEQUENCE hibernate_sequence START 1 INCREMENT 1;

CREATE TABLE classes
(
    code VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (code)
);

CREATE TABLE sections
(
    codes text[],
    name VARCHAR(255),
    PRIMARY KEY (name)
);

INSERT INTO SECTIONS  VALUES  (ARRAY['GC11', 'GC12'], 'section 1');
INSERT INTO SECTIONS  VALUES  (ARRAY['GC21', 'GC22'], 'section 2');