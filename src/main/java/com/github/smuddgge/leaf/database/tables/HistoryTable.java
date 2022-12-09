package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.HistoryRecord;
import com.github.smuddgge.leaf.database.sqlite.SQLiteDatabase;
import com.github.smuddgge.leaf.database.sqlite.SQLiteTable;
import com.github.smuddgge.leaf.events.PlayerHistoryEventType;
import com.github.smuddgge.leaf.utility.DateAndTime;

import java.util.*;

/**
 * Represents the history table in the database.
 */
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
     * @param server     The server's name.
     * @param eventType  The history event type.
     */
    public void insertHistory(String playerUuid, String server, PlayerHistoryEventType eventType) {
        HistoryRecord historyRecord = new HistoryRecord();

        historyRecord.uuid = UUID.randomUUID().toString();
        historyRecord.playerUuid = playerUuid;
        historyRecord.event = eventType.toString();
        historyRecord.date = DateAndTime.getNow();
        historyRecord.timeStamp = String.valueOf(System.currentTimeMillis());
        historyRecord.server = server;

        this.insertRecord(historyRecord);

        // Limit history
        this.limitHistory(playerUuid, ConfigDatabase.get().getInteger("player_history_limit", 20));
    }

    /**
     * Used to remove access history from a player.
     *
     * @param playerUuid The players uuid.
     * @param limit      The amount of history to limit to.
     */
    public void limitHistory(String playerUuid, int limit) {
        ArrayList<Record> records = this.getRecord("playerUuid", playerUuid);
        if (records.size() <= limit) return;
        int toRemove = records.size() - limit;

        Map<Long, HistoryRecord> map = new TreeMap<>();
        for (Record record : records) {
            HistoryRecord historyRecord = (HistoryRecord) record;
            map.put(Long.parseLong(historyRecord.timeStamp), historyRecord);
        }

        for (int i = 0; i < toRemove; i++) {
            Long key = map.keySet().stream().toList().get(0);
            HistoryRecord historyRecord = map.get(key);

            map.remove(key);
            this.removeRecord("uuid", historyRecord.uuid);
        }
    }

    /**
     * Used to get the records ordered.
     *
     * @param key   Key of the records to match.
     * @param value Value of the records to match.
     * @return Sorted list of records
     */
    public ArrayList<HistoryRecord> getRecordOrdered(String key, Object value) {
        Map<Long, HistoryRecord> map = new TreeMap<>(Collections.reverseOrder());
        for (Record record : this.getRecord(key, value)) {
            HistoryRecord historyRecord = (HistoryRecord) record;
            map.put(Long.parseLong(historyRecord.timeStamp), historyRecord);
        }

        ArrayList<HistoryRecord> records = new ArrayList<>();
        for (Map.Entry<Long, HistoryRecord> entry : map.entrySet()) {
            records.add(entry.getValue());
        }

        return records;
    }
}
