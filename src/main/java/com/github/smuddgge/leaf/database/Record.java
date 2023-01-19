package com.github.smuddgge.leaf.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a record in a database
 */
public class Record {

    /**
     * Used to get the class as a list of fields
     *
     * @return List of fields
     */
    public ArrayList<Field> getFields() {
        ArrayList<Field> fields = new ArrayList<>();

        // Loop though class fields
        for (java.lang.reflect.Field field : this.getClass().getFields()) {
            if (!field.isAnnotationPresent(FieldAnnotation.class)) continue;

            // Create new database field
            Field databaseField = new Field(
                    field.getName(),
                    FieldValueType.get(field.getType()),
                    field.getAnnotation(FieldAnnotation.class).fieldKeyType()
            );

            // Update the value if it has a value
            Object value = null;
            try {
                value = field.get(this);
            } catch (IllegalAccessException ignored) {
            }
            databaseField.setValue(value);

            // If it's a foreign key, update references
            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

                databaseField.setReferences(
                        foreignKey.tableReferenceName(),
                        foreignKey.tableReferenceValue()
                );
            } else {
                // If it doesn't contain a foreign key annotation but is described as one, throw an error
                if (field.getAnnotation(FieldAnnotation.class).fieldKeyType() == FieldKeyType.FOREIGN) {
                    throw new MissingForeignKeyAnnotationError();
                }
            }

            // Add field to the list
            fields.add(databaseField);
        }

        return fields;
    }

    /**
     * Used to get a field.
     *
     * @param fieldKey The fields key.
     * @return The requested field.
     */
    public Field getField(String fieldKey) {
        for (Field field : this.getFields()) {
            if (Objects.equals(field.getKey(), fieldKey)) return field;
        }
        return null;
    }

    /**
     * Used to get the primary key
     *
     * @return null if there are no primary keys
     */
    public Field getPrimaryKey() {
        for (Field field : this.getFields()) {
            if (field.getKeyType() == FieldKeyType.PRIMARY) return field;
        }
        return null;
    }

    /**
     * Used to append a result set to a record
     *
     * @param results Results to append to the record
     * @throws SQLException           Result exception
     * @throws NoSuchFieldException   If the field in the record doesn't exist
     * @throws IllegalAccessException If the field isn't able to be set
     */
    public void appendResult(ResultSet results) throws SQLException, NoSuchFieldException, IllegalAccessException {
        ArrayList<Field> fields = new ArrayList<>();

        int index = 1;
        for (Field field : this.getFields()) {
            if (field.getValueType() == FieldValueType.STRING) {
                this.getClass().getField(field.getKey()).set(this, results.getString(index));
            }
            if (field.getValueType() == FieldValueType.INTEGER) {
                this.getClass().getField(field.getKey()).set(this, results.getInt(index));
            }

            index++;
        }
    }

    /**
     * Used to toggle a boolean in a record.
     *
     * @param fieldKey The key of a field.
     */
    public void toggleBoolean(String fieldKey) {
        Field field = this.getField(fieldKey);
        if (field.getValue().equals("false")) field.setValue("true");
        if (field.getValue().equals("true")) field.setValue("false");
    }
}
