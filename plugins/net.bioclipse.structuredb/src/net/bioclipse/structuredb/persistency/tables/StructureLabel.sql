CREATE TABLE StructureLabel (
    structureId          VARCHAR(36) NOT NULL,
    labelId              VARCHAR(36) NOT NULL,

    PRIMARY KEY (structureId, labelId)
);

ALTER TABLE StructureLabel 
    ADD FOREIGN KEY (structureId) 
    REFERENCES Structure(id) ON DELETE CASCADE;
ALTER TABLE StructureLabel 
    ADD FOREIGN KEY (labelId) 
    REFERENCES Label(id) ON DELETE CASCADE;
