CREATE TABLE Structure (
	id                VARCHAR(36) NOT NULL,
	baseObject        VARCHAR(36) NOT NULL,
    name              VARCHAR(50) NOT NULL,
	folder            VARCHAR(36),
	fingerprintString VARCHAR(2000),
	smiles            VARCHAR(5000),
	molecule          VARCHAR,

	PRIMARY KEY (id)
);

ALTER TABLE Structure ADD UNIQUE (id); 
ALTER TABLE Structure ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;
ALTER TABLE Structure ADD FOREIGN KEY (folder) REFERENCES Folder(id) ON DELETE SET NULL;
