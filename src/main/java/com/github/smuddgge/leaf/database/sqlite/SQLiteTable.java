package com.github.smuddgge.leaf.database.sqlite;

import com.github.smuddgge.leaf.database.Field;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <h2>Represents a sqlite table</h2>
 * Tables are used to get ad update records
 */
public abstract class SQLiteTable implements Table {

    /**
     * <h2>The instance of the database</h2>
     * This instance will be used to query the database
     */
    private final SQLiteDatabase database;

    /**
     * Used to register the table with a database
     * Note this does not create the table in the database
     *
     * @param database The instance of the database to query
     */
    public SQLiteTable(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public abstract String getName();

    @Override
    public ArrayList<Field> getFields() {
        return this.getRecord().getFields();
    }

    @Override
    public ArrayList<Record> getRecord(String key, Object value) {

        String statement = "SELECT * FROM `" + this.getName() + "` WHERE " + key + " = '" + value + "'";

        ResultSet results = this.database.executeQuery(statement);
        if (results == null) return null;

        ArrayList<Record> records = new ArrayList<>();

        try {
            while (results.next()) {
                Record record = this.getRecord();
                record.appendResult(results);
                records.add(record);
            }
        } catch (SQLException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            return null;
        }

        return records;
    }

    @Override
    public boolean insertRecord(Record record) {
        if (this.getRecord(record.getPrimaryKey().getKey(), record.getPrimaryKey().getValue()).size() == 0) {
            return this.addRecord(record);
        }

        return this.updateRecord(record);
    }

    /**
     * Used to get the instance of an empty record
     *
     * @return Instance of an empty record
     */
    public abstract Record getRecord();

    /**
     * Used to add a record to the table using INSERT
     *
     * @param record Record to add to the table
     * @return If it was successfully inserted
     */
    private boolean addRecord(Record record) {
        // "INSERT INTO `Table` (key) VALUES (value);"
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO `").append(this.getName()).append("` (");

        // Append the keys
        int index = 0;
        for (Field field : record.getFields()) {
            index++;
            builder.append(field.getKey());
            if (record.getFields().size() != index) builder.append(", ");
        }

        builder.append(") VALUES (");

        // Append the values
        index = 0;
        for (Field field : record.getFields()) {
            index++;
            builder.append(field.getValueFormatted());
            if (record.getFields().size() != index) builder.append(", ");
        }

        builder.append(");");

        return this.database.executeStatement(builder.toString());
    }

    /**
     * Used to update a record that already exists in the database
     *
     * @param record Record to update
     * @return If it was successfully updated
     */
    private boolean updateRecord(Record record) {
        // "UPDATE `Table` SET key = value, key = value WHERE primary = value;"
        StringBuilder builder = new StringBuilder();

        builder.append("UPDATE `").append(this.getName()).append("` SET ");

        // Add keys and values
        int index = 0;
        for (Field field : record.getFields()) {
            index++;
            builder.append(field.getKey()).append(" = ").append(field.getValueFormatted());
            if (record.getFields().size() != index) builder.append(", ");
        }

        builder.append(" WHERE ").append(record.getPrimaryKey().getKey()).append(" = ");

        // Add the primary key
        builder.append(record.getPrimaryKey().getValueFormatted()).append(";");

        return this.database.executeStatement(builder.toString());
    }

    /**
     * Used to sum field values.
     *
     * @param field The field to sum.
     * @return The value of the summed up fields.
     */
    public int sum(String field) {
        String statement = "SELECT SUM(" + field + ")" +
                " FROM " + this.getName();

        ResultSet set = this.database.executeQuery(statement);

        try {
            return set.getInt("sum(" + field + ")");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return -1;
    }
}
