CREATE TABLE PropertyChoice (
	id               VARCHAR(36) NOT NULL,
	baseObject       VARCHAR(36) NOT NULL,
	val              VARCHAR(50) NOT NULL,
    choicePropertyId VARCHAR(36),
	PRIMARY KEY (id)
);

ALTER TABLE PropertyChoice ADD UNIQUE (id);
ALTER TABLE PropertyChoice 
	ADD FOREIGN KEY (baseObject)
    REFERENCES BaseObject(id)
    ON DELETE CASCADE;
ALTER TABLE PropertyChoice 
	ADD FOREIGN KEY (choicePropertyId) 
    REFERENCES ChoiceProperty(id) 
    ON DELETE CASCADE;