DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS coordinates CASCADE;
DROP TABLE IF EXISTS location CASCADE;

CREATE TABLE coordinates
(
    id    SERIAL               PRIMARY KEY,
    x     BIGINT               NOT NULL,
    y     DOUBLE PRECISION     NOT NULL
);

CREATE TABLE location
(
    id      SERIAL                PRIMARY KEY,
    name    VARCHAR(255),
    x       DOUBLE PRECISION,
    y       DOUBLE PRECISION,
    z       DOUBLE PRECISION
);

CREATE TABLE person
(
    id              SERIAL         PRIMARY KEY,
    name            VARCHAR(255)   NOT NULL,
    creationDate    DATE           NOT NULL,
    height          BIGINT         NOT NULL,
    passportID      VARCHAR(255)   NOT NULL UNIQUE,
    hairColor       VARCHAR(255),
    birthday        DATE           NOT NULL,
    coordinates_id  INT, 
    location_id     INT, 

    FOREIGN KEY (coordinates_id)
        REFERENCES coordinates (id)
                ON DELETE SET NULL,
                
    FOREIGN KEY (location_id)
        REFERENCES location (id)
                ON DELETE SET NULL
);