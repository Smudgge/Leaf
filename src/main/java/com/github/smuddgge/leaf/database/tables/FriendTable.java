package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * Used to get a friend record given the two players.
     *
     * @param playerUuid The players uuid.
     * @param friendUuid The player friends uuid.
     * @return The requested friend record.
     */
    public FriendRecord getFriend(String playerUuid, String friendUuid) {
        for (Record record : this.getRecord("playerUuid", playerUuid)) {
            FriendRecord friendRecord = (FriendRecord) record;
            if (Objects.equals(friendRecord.friendPlayerUuid, friendUuid)) return friendRecord;
        }
        return null;
    }

    /**
     * Used to get a players friend list.
     *
     * @param playerUuid The players uuid.
     * @return The requested list of friend records.
     */
    public List<FriendRecord> getFriendList(String playerUuid) {
        List<FriendRecord> friendRecords = new ArrayList<>();
        for (Record record : this.getRecord("playerUuid", playerUuid)) {
            friendRecords.add((FriendRecord) record);
        }
        return friendRecords;
    }
}
