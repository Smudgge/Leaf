package com.github.smuddgge.leaf.database;

/**
 * Represents a database
 */
public interface Database {

    /**
     * Used to set up the database
     *
     * @return True if connected successfully to the database
     */
    boolean setup();

    /**
     * Used to create a table in the database if it doesn't exist
     * This method may also register the table in the class
     *
     * @return True if successfully created a table
     */
    boolean createTable(Table table);

    /**
     * Used to get a table from the database
     *
     * @param name Name of the table
     * @return The instance of the table
     */
    Table getTable(String name);
}
