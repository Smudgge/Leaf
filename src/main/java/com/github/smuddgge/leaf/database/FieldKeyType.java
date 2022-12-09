package com.github.smuddgge.leaf.database;

/**
 * <h2>The type of field</h2>
 * If the key type is FOREIGN there must also
 * be a {@link ForeignKey} annotation to specify the references
 * A default key type would be FIELD
 */
public enum FieldKeyType {
    PRIMARY, FOREIGN, FIELD
}
