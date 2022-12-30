package com.github.smuddgge.leaf.database;

import java.util.ArrayList;

/**
 * Represents a table in the database
 */
public interface Table {

    /**
     * Used to get the name of the table
     *
     * @return The name of the table
     */
    String getName();

    /**
     * Used to get the fields the table has
     *
     * @return List of table fields
     */
    ArrayList<Field> getFields();

    /**
     * Used to get the fields that are a specific
     * key type in the table.
     *
     * @param fieldKeyType The type of key to get
     * @return List of fields that are primary keys
     */
    default ArrayList<Field> getFields(FieldKeyType fieldKeyType) {
        ArrayList<Field> fields = new ArrayList<>();

        for (Field field : this.getFields()) {
            if (field.getKeyType() == fieldKeyType) fields.add(field);
        }

        return fields;
    }

    /**
     * Used to get records from the database that
     * contain the key and value
     *
     * @param key   The key to match
     * @param value The value to match
     * @return A list of records that match the selector
     */
    ArrayList<Record> getRecord(String key, Object value);


    /**
     * Used to get all the records in the database.
     */
    ArrayList<Record> getAllRecords();

    /**
     * Used to insert a record into the table
     * if the record already exists it will be overwritten
     *
     * @param record Record to insert
     * @return True if inserted successfully
     */
    boolean insertRecord(Record record);

    /**
     * Used to remove records from the table.
     *
     * @param key   The key of a record to match.
     * @param value The value of a record to match.
     * @return True if removed successfully.
     */
    boolean removeRecord(String key, Object value);
}
