CREATE TABLE PropertyChoice (
	id         VARCHAR(36) NOT NULL,
	baseObject VARCHAR(36) NOT NULL,
	val        VARCHAR(50) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE PropertyChoice ADD UNIQUE (id);
ALTER TABLE PropertyChoice ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;
