CREATE TABLE DBMolecule (
    id                   VARCHAR(36) NOT NULL,
    baseObject           VARCHAR(36) NOT NULL,
    name                 VARCHAR(50) NOT NULL,
    persistedFingerprint BINARY,
    smiles               VARCHAR(5000),
    molecule             VARCHAR,

    PRIMARY KEY (id)
);

CREATE ALIAS BITAND FOR "net.bioclipse.hsqldb.HsqldbHelper.bitAnd"
ALTER TABLE DBMolecule ADD UNIQUE (id); 
ALTER TABLE DBMolecule ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;
