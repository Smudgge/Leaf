package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;

public class FriendMailTable extends SQLiteTable {

    /**
     * Used to register the table with a database
     * Note this does not create the table in the database
     *
     * @param database The instance of the database to query
     */
    public FriendMailTable(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public Record getRecord() {
        return new FriendMailRecord();
    }

    @Override
    public String getName() {
        return "FriendMail";
    }
}
