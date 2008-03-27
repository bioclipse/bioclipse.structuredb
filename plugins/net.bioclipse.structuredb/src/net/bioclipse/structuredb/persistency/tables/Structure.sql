CREATE TABLE Structure (
	id                VARCHAR(36) NOT NULL,
	baseObject        VARCHAR(36) NOT NULL,
	library           VARCHAR(36),
	fingerprintString VARCHAR(2000),
	smiles            VARCHAR(5000),
	molecule          VARCHAR,

	PRIMARY KEY (id)
);

ALTER TABLE Structure ADD UNIQUE (id); 
ALTER TABLE Structure ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id);
ALTER TABLE Structure ADD FOREIGN KEY (library)    REFERENCES Library(id);
