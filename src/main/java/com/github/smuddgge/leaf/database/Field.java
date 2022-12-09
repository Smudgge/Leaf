package com.github.smuddgge.leaf.database;

/**
 * Represents a field in the database
 */
public class Field {

    /**
     * The type of field
     * This is used when creating the table so the database
     * knows what type of value will be held
     */
    private final FieldValueType fieldType;
    private final FieldKeyType keyType;

    private final String key;
    private Object value;

    /**
     * If the field is a foreign key
     */
    private String referenceTableName;
    private String referenceValueName;

    /**
     * Used to create a new field
     *
     * @param key       The key of the field
     * @param valueType The type of value that the field will contain
     */
    public Field(String key, FieldValueType valueType, FieldKeyType keyType) {
        this.key = key;
        this.fieldType = valueType;
        this.keyType = keyType;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * If the key is a foreign key, it will have a reference to another table
     * This is where the program will update the field references
     *
     * @param tableName The name of the referenced table
     * @param valueName The name of the referenced Value
     */
    public void setReferences(String tableName, String valueName) {
        this.referenceTableName = tableName;
        this.referenceValueName = valueName;
    }

    public FieldValueType getValueType() {
        return this.fieldType;
    }

    public FieldKeyType getKeyType() {
        return this.keyType;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    /**
     * Used to get the value as a formatted string.
     * If the field value type is a string it will apply quotations
     *
     * @return Formatted string
     */
    public String getValueFormatted() {
        if (this.fieldType == FieldValueType.STRING) return "'" + this.value + "'";
        return String.valueOf(this.value);
    }

    public String getReferenceTableName() {
        return referenceTableName;
    }

    public String getReferenceValueName() {
        return referenceValueName;
    }
}
