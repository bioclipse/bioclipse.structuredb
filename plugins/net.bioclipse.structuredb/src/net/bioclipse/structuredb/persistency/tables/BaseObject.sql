CREATE CACHED TABLE BaseObject (
	id   VARCHAR(36) NOT NULL,
	created TIMESTAMP,
	edited TIMESTAMP,
	creator VARCHAR(36),
	lastEditor VARCHAR(36),

	PRIMARY KEY (id)
);

ALTER TABLE BaseObject ADD UNIQUE (id); 
ALTER TABLE BaseObject ADD FOREIGN KEY (creator)     REFERENCES User(id);
ALTER TABLE BaseObject ADD FOREIGN KEY (lastEditor)  REFERENCES User(id);
