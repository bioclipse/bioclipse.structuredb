CREATE TABLE Structure (
    id                   VARCHAR(36) NOT NULL,
    baseObject           VARCHAR(36) NOT NULL,
    name                 VARCHAR(50) NOT NULL,
    persistedFingerprint BINARY,
    smiles               VARCHAR(5000),
    molecule             VARCHAR,

    PRIMARY KEY (id)
);

CREATE ALIAS BITAND FOR "net.bioclipse.hsqldb.HsqldbHelper.bitAnd"
ALTER TABLE Structure ADD UNIQUE (id); 
ALTER TABLE Structure ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;
