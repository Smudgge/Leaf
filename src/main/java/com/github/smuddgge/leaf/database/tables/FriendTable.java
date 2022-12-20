package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;

public class FriendTable extends SQLiteTable {

    /**
     * Used to register the table with a database
     * Note this does not create the table in the database
     *
     * @param database The instance of the database to query
     */
    public FriendTable(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public Record getRecord() {
        return new FriendRecord();
    }

    @Override
    public String getName() {
        return "Friend";
    }
}
