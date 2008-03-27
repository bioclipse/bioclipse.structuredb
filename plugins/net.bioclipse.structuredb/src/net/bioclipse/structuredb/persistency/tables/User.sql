CREATE TABLE User (
	id         VARCHAR(36) NOT NULL,
	userName   VARCHAR(50),
	passWord   VARCHAR(50),
	sudoer     BOOLEAN,
	baseObject VARCHAR(36) NOT NULL,

	PRIMARY KEY (id)
);

ALTER TABLE User ADD UNIQUE (id); 
ALTER TABLE User ADD FOREIGN KEY (baseObject) REFERENCES BaseObject(id);
