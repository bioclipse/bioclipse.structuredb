CREATE TABLE StructureAnnotation (
    structureId  VARCHAR(36) NOT NULL,
    annotationId VARCHAR(36) NOT NULL,

    PRIMARY KEY (structureId, annotationId)
);

ALTER TABLE StructureAnnotation ADD FOREIGN KEY (structureId) REFERENCES Structure(id) ON DELETE CASCADE;
ALTER TABLE StructureAnnotation ADD FOREIGN KEY (annotationId) REFERENCES Annotation(id) ON DELETE CASCADE;
