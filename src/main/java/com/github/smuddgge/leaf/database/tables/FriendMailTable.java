package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendMailRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;

import java.util.Objects;

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

    /**
     * Used to get the latest mail sent.
     *
     * @param fromUuid The uuid of the player sending the mail.
     * @param toUuid   The uuid of the player the mail was sent to.
     * @return Null if there are none.
     */
    public FriendMailRecord getLatest(String fromUuid, String toUuid) {
        for (Record record : this.getRecord("friendFromUuid", fromUuid)) {
            FriendMailRecord friendMailRecord = (FriendMailRecord) record;

            if (!Objects.equals(friendMailRecord.friendToUuid, toUuid)) continue;

            return friendMailRecord;
        }
        return null;
    }
}
