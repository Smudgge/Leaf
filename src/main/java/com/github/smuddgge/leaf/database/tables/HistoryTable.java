package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.HistoryRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;
import com.github.smuddgge.leaf.events.PlayerHistoryEventType;
import com.github.smuddgge.leaf.utility.DateAndTime;

import java.util.UUID;

public class HistoryTable extends SQLiteTable {

    /**
     * Used to register the table with a database
     * Note this does not create the table in the database
     *
     * @param database The instance of the database to query
     */
    public HistoryTable(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public String getName() {
        return "History";
    }

    @Override
    public Record getRecord() {
        return new HistoryRecord();
    }

    /**
     * Used to insert history into the table.
     *
     * @param playerUuid The players uuid.
     * @param server The server's name.
     * @param eventType The history event type.
     */
    public void insertHistory(String playerUuid, String server, PlayerHistoryEventType eventType) {
        HistoryRecord historyRecord = new HistoryRecord();

        historyRecord.uuid = UUID.randomUUID().toString();
        historyRecord.playerUuid = playerUuid;
        historyRecord.event = eventType.toString();
        historyRecord.date = DateAndTime.getNow();
        historyRecord.server = server;

        this.insertRecord(historyRecord);
    }
}
