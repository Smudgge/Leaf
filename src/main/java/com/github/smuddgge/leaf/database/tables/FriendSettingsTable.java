package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendSettingsRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;

import java.util.ArrayList;
import java.util.UUID;

public class FriendSettingsTable extends SQLiteTable {

    /**
     * Used to register the table with a database
     * Note this does not create the table in the database
     *
     * @param database The instance of the database to query
     */
    public FriendSettingsTable(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public Record getRecord() {
        return new FriendSettingsRecord();
    }

    @Override
    public String getName() {
        return "FriendSettings";
    }

    /**
     * Used to get a players friend settings.
     *
     * @param playerUuid The players uuid.
     * @return The instance of the friend settings.
     */
    public FriendSettingsRecord getSettings(String playerUuid) {
        ArrayList<Record> result = this.getRecord("playerUuid", playerUuid);
        if (result.isEmpty()) {
            FriendSettingsRecord friendSettingsRecord = new FriendSettingsRecord();
            friendSettingsRecord.playerUuid = playerUuid;
            friendSettingsRecord.uuid = UUID.randomUUID().toString();
            this.insertRecord(friendSettingsRecord);
            return friendSettingsRecord;
        }
        return (FriendSettingsRecord) result.get(0);
    }
}
