CREATE CACHED TABLE RealNumberAnnotation (
    id                 VARCHAR(36) NOT NULL,
    val                FLOAT       NOT NULL,
    annotation         VARCHAR(36) NOT NULL,
    realNumberProperty VARCHAR(36) NOT NULL,

    PRIMARY KEY (id)
);

ALTER TABLE RealNumberAnnotation ADD UNIQUE (id);
ALTER TABLE RealNumberAnnotation 
    ADD FOREIGN KEY (annotation) 
    REFERENCES Annotation(id) 
    ON DELETE CASCADE;
ALTER TABLE RealNumberAnnotation
    ADD FOREIGN KEY (realNumberProperty)
    REFERENCES RealNumberProperty(id)
    ON DELETE CASCADE;