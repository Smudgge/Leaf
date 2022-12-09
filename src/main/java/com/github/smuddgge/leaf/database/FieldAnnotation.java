package com.github.smuddgge.leaf.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to annotate fields to specify what type of
 * key they are in {@link Record}'s
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAnnotation {

    /**
     * @return The type of field, defaulting to FIELD
     */
    FieldKeyType fieldKeyType() default FieldKeyType.FIELD;
}
