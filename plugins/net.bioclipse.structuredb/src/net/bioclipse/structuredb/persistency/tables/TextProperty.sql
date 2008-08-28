CREATE TABLE TextProperty (
	id         VARCHAR(36) NOT NULL,
    name       VARCHAR(50) NOT NULL,
	baseObject VARCHAR(36) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE TextProperty ADD UNIQUE (id);
ALTER TABLE TextProperty ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;