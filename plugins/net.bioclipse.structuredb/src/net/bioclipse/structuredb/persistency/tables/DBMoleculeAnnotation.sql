CREATE CACHED TABLE DBMoleculeAnnotation (
    dBMoleculeId VARCHAR(36) NOT NULL,
    annotationId VARCHAR(36) NOT NULL,

    PRIMARY KEY (dBMoleculeId, annotationId)
);

ALTER TABLE DBMoleculeAnnotation ADD FOREIGN KEY (dBMoleculeId) REFERENCES DBMolecule(id) ON DELETE CASCADE;
ALTER TABLE DBMoleculeAnnotation ADD FOREIGN KEY (annotationId) REFERENCES Annotation(id) ON DELETE CASCADE;
