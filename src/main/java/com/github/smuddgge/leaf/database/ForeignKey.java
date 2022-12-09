package com.github.smuddgge.leaf.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to annotate foreign keys to
 * state the table reference
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {

    /**
     * @return The name of the table the foreign
     * key is from.
     */
    String tableReferenceName();

    /**
     * @return The name of the field in the
     * reference table
     */
    String tableReferenceValue();
}
