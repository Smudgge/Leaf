package com.github.smuddgge.leaf.database.sqlite;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.database.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a sqlite database
 */
public class SQLiteDatabase implements Database {

    private final File folder;

    /**
     * The path from resources folder
     */
    private final String fileName;

    /**
     * Connection to the sqlite database
     */
    private Connection connection;

    /**
     * Represents if the database should be used
     * Can be set to false if an error occurs
     */
    private boolean usingDatabase;

    /**
     * The list of registered tables
     */
    private final ArrayList<SQLiteTable> tables = new ArrayList<>();

    /**
     * Used to create a connection to a sqlite database
     *
     * @param fileName The name of the database file
     *                 without the extension specified.
     */
    public SQLiteDatabase(File folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }

    @Override
    public boolean setup() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            this.usingDatabase = false;
            return false;
        }

        // Create the directory and file if it doesn't exist
        File file = new File(folder.getAbsolutePath(), File.separator);

        if (file.mkdir()) {
            MessageManager.log("[Database] Created directory for " + this.fileName + ".sqlite3");
        }

        // Try to connect to the database
        try {

            String url = "jdbc:sqlite:" + file.getAbsolutePath() + File.separator + this.fileName + ".sqlite3";

            this.connection = DriverManager.getConnection(url);

            if (this.connection == null) {
                MessageManager.warn("[Database] Unable to connect to the database");
                this.usingDatabase = false;
                return false;
            }

        } catch (SQLException exception) {
            MessageManager.warn("[Database] Unable to connect to the database");
            exception.printStackTrace();
            this.usingDatabase = false;
            return false;
        }

        this.usingDatabase = true;
        MessageManager.log("[Database] Connected to the sqlite database");
        return true;
    }

    @Override
    public boolean createTable(Table table) {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE IF NOT EXISTS `").append(table.getName()).append("` (");

        // Build the primary keys
        for (Field field : table.getFields(FieldKeyType.PRIMARY)) {
            String fieldType = SQLiteDatabase.getSqliteType(field.getValueType());

            if (fieldType == null) continue;

            builder.append("`{key}` {type} PRIMARY KEY, "
                    .replace("{key}", field.getKey())
                    .replace("{type}", fieldType)
            );
        }

        // Build other fields
        int index = 0;
        for (Field field : table.getFields(FieldKeyType.FIELD)) {
            index++;
            String fieldType = SQLiteDatabase.getSqliteType(field.getValueType());

            if (fieldType == null) continue;

            builder.append("`{key}` {type}"
                    .replace("{key}", field.getKey())
                    .replace("{type}", fieldType)
            );
            if (index != table.getFields(FieldKeyType.FIELD).size()) builder.append(", ");
        }

        // Build the foreign keys
        for (Field field : table.getFields(FieldKeyType.FOREIGN)) {
            String fieldType = SQLiteDatabase.getSqliteType(field.getValueType());

            if (fieldType == null) continue;

            builder.append(", `{key}` {type}"
                    .replace("{key}", field.getKey())
                    .replace("{type}", fieldType)
            );
        }

        for (Field field : table.getFields(FieldKeyType.FOREIGN)) {
            String fieldType = SQLiteDatabase.getSqliteType(field.getValueType());

            if (fieldType == null) continue;

            builder.append(", FOREIGN KEY({key}) REFERENCES {table}({value})"
                    .replace("{key}", field.getKey())
                    .replace("{table}", field.getReferenceTableName())
                    .replace("{value}", field.getReferenceValueName())
            );
        }

        builder.append(");");

        // Execute the statement
        boolean successful = this.executeStatement(builder.toString());

        // Add table
        this.tables.add((SQLiteTable) table);

        return successful;
    }

    @Override
    public SQLiteTable getTable(String name) {
        for (SQLiteTable table : this.tables) {
            if (Objects.equals(table.getName(), name)) return table;
        }
        return null;
    }

    /**
     * Used to execute a statement
     *
     * @param sql Statement to execute
     * @return True if successful
     */
    public boolean executeStatement(String sql) {
        if (!this.usingDatabase) {
            MessageManager.warn("[Database] Tried to use the database when not connected");
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException exception) {
            MessageManager.warn("[Database] Unable to execute statement: " + sql);
            exception.printStackTrace();
            this.usingDatabase = false;
            return false;
        }

        return true;
    }

    /**
     * Used to execute a query and return the results
     *
     * @param sql Statement to execute
     * @return Result set
     */
    public ResultSet executeQuery(String sql) {
        if (!this.usingDatabase) {
            MessageManager.warn("[Database] Tried to use the database when not connected");
            return null;
        }

        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException exception) {
            MessageManager.warn("[Database] Unable to execute statement: " + sql);
            exception.printStackTrace();
            this.usingDatabase = false;
        }

        return null;
    }

    /**
     * Used to turn field value types into sqlite value types
     * For example, 'string' turns into 'text'
     *
     * @param fieldValueType The value type to convert
     * @return String representing the sqlite type
     */
    public static String getSqliteType(FieldValueType fieldValueType) {
        if (fieldValueType == FieldValueType.INTEGER) return "INTEGER";
        if (fieldValueType == FieldValueType.STRING) return "text";
        return null;
    }
}
