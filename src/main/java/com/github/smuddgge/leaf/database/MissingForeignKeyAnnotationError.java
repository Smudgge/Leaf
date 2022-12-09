package com.github.smuddgge.leaf.database;

/**
 * Represents an error thrown when a record is a foreign key but doesn't
 * include a foreign key annotation to reference a table.
 */
public class MissingForeignKeyAnnotationError extends RuntimeException {

    /**
     * Used to create a {@link MissingForeignKeyAnnotationError}
     */
    public MissingForeignKeyAnnotationError() {
        super("Missing foreign key annotation for a foreign key!");
    }
}
