package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.database.records.HistoryRecord;
import com.github.smuddgge.leaf.listeners.PlayerHistoryEventType;
import com.github.smuddgge.leaf.utility.DateAndTime;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents the history table in the database.
 */
public class HistoryTable extends TableAdapter<HistoryRecord> {

    @Override
    public @NotNull String getName() {
        return "History";
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
        List<HistoryRecord> historyRecordList = this.getRecordList(
                new Query().match("playerUuid", playerUuid)
        );

        if (historyRecordList.size() <= limit) return;
        int toRemove = historyRecordList.size() - limit;

        Map<Long, HistoryRecord> map = new TreeMap<>();
        for (HistoryRecord historyRecord : historyRecordList) {
            map.put(Long.parseLong(historyRecord.timeStamp), historyRecord);
        }

        for (int i = 0; i < toRemove; i++) {
            Long key = map.keySet().stream().toList().get(0);
            HistoryRecord historyRecord = map.get(key);

            map.remove(key);
            this.removeRecord(historyRecord);
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

        for (HistoryRecord historyRecord : this.getRecordList(new Query().match(key, value))) {
            map.put(Long.parseLong(historyRecord.timeStamp), historyRecord);
        }

        ArrayList<HistoryRecord> records = new ArrayList<>();
        for (Map.Entry<Long, HistoryRecord> entry : map.entrySet()) {
            records.add(entry.getValue());
        }

        return records;
    }
}
