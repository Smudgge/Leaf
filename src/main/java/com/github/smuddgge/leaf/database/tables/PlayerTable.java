package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;
import com.github.smuddgge.leaf.datatype.User;

import java.util.ArrayList;

/**
 * Represents the player table in the database.
 */
public class PlayerTable extends SQLiteTable {

    /**
     * Used to register the table with a database.
     *
     * @param database The instance of the database.
     */
    public PlayerTable(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public String getName() {
        return "Player";
    }

    @Override
    public Record getRecord() {
        return new PlayerRecord();
    }

    /**
     * Used to update a user in the database.
     *
     * @param user User to update.
     */
    public void updatePlayer(User user) {
        ArrayList<Record> playerRecords = this.getRecord("uuid", user.getUniqueId().toString());
        PlayerRecord playerRecord = new PlayerRecord();

        if (playerRecords.isEmpty()) {
            playerRecord.uuid = user.getUniqueId().toString();
            playerRecord.name = user.getName();
        } else {
            playerRecord = (PlayerRecord) playerRecords.get(0);
        }

        this.insertRecord(playerRecord);
    }
}
