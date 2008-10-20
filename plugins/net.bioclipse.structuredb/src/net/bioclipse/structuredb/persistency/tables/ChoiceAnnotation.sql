CREATE TABLE ChoiceAnnotation (
	id             VARCHAR(36) NOT NULL,
    val            VARCHAR(50) NOT NULL,
	annotation     VARCHAR(36) NOT NULL,
	choiceProperty VARCHAR(36) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE ChoiceAnnotation ADD UNIQUE (id);
ALTER TABLE ChoiceAnnotation 
	ADD FOREIGN KEY (annotation)     
	REFERENCES Annotation(id) 
	ON DELETE CASCADE;
ALTER TABLE ChoiceAnnotation 
	ADD FOREIGN KEY (choiceProperty) 
	REFERENCES ChoiceProperty(id)
	ON DELETE CASCADE;