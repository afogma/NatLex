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
