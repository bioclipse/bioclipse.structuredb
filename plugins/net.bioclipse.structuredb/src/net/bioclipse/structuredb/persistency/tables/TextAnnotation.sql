CREATE CACHED TABLE TextAnnotation (
	id           VARCHAR(36) NOT NULL,
    val          VARCHAR(50) NOT NULL,
	annotation   VARCHAR(36) NOT NULL,
	textProperty VARCHAR(36) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE TextAnnotation ADD UNIQUE (id);
ALTER TABLE TextAnnotation 
	ADD FOREIGN KEY (annotation) 
	REFERENCES Annotation(id) 
	ON DELETE CASCADE;
ALTER TABLE TextAnnotation
	ADD FOREIGN KEY (textProperty)
	REFERENCES TextProperty(id)
    ON DELETE CASCADE;