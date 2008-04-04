CREATE TABLE Library (
	id         VARCHAR(36) NOT NULL,
    name       VARCHAR(50) NOT NULL,
	baseObject VARCHAR(36) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE Library ADD UNIQUE (id);
ALTER TABLE Library ADD UNIQUE (name); 
ALTER TABLE Library ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;
