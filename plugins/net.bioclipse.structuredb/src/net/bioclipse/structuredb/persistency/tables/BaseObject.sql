CREATE CACHED TABLE BaseObject (
    id VARCHAR(36) NOT NULL,
    created TIMESTAMP,
    edited TIMESTAMP,

    PRIMARY KEY (id)
);

ALTER TABLE BaseObject ADD UNIQUE (id);
