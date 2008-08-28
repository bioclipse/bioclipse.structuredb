CREATE TABLE ChoiceProperty (
	id         VARCHAR(36) NOT NULL,
    name       VARCHAR(50) NOT NULL,
	baseObject VARCHAR(36) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE ChoiceProperty ADD UNIQUE (id);
ALTER TABLE ChoiceProperty ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id) ON DELETE CASCADE;