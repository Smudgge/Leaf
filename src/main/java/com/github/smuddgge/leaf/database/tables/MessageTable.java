package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.database.records.MessageRecord;
import com.github.smuddgge.leaf.utility.DateAndTime;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.interfaces.TableAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents the message table.
 */
public class MessageTable extends TableAdapter<MessageRecord> {

    @Override
    public @NotNull String getName() {
        return "Message";
    }

    /**
     * Used to get the records ordered.
     *
     * @param query The query for the database records.
     * @return Sorted list of records.
     */
    public List<MessageRecord> getRecordOrdered(Query query) {
        Map<Long, MessageRecord> map = new TreeMap<>(Collections.reverseOrder());

        for (MessageRecord messageRecord : this.getRecordList(query)) {
            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        ArrayList<MessageRecord> records = new ArrayList<>();
        for (Map.Entry<Long, MessageRecord> entry : map.entrySet()) {
            records.add(entry.getValue());
        }

        return records;
    }

    /**
     * Used to insert a message into the database table.
     *
     * @param fromUuid The player that the message is sent from.
     * @param toUuid   The player that the message is sent to.
     * @param message  The content of the message.
     */
    public void insertMessage(String fromUuid, String toUuid, String message) {
        MessageRecord messageRecord = new MessageRecord();

        messageRecord.uuid = UUID.randomUUID().toString();
        messageRecord.message = message;
        messageRecord.date = DateAndTime.getNow();
        messageRecord.timeStamp = String.valueOf(System.currentTimeMillis());
        messageRecord.fromPlayerUuid = fromUuid;
        messageRecord.toPlayerUuid = toUuid;

        this.insertRecord(messageRecord);
    }

    /**
     * Used to remove access messages from a player sender.
     *
     * @param playerUuid The senders players uuid.
     * @param limit      The amount of messages to limit to.
     */
    public void limitMessages(String playerUuid, int limit) {
        List<MessageRecord> messageRecordList = this.getRecordList(
                new Query().match("fromPlayerUuid", playerUuid)
        );

        if (messageRecordList.size() <= limit) return;
        int toRemove = messageRecordList.size() - limit;

        Map<Long, MessageRecord> map = new TreeMap<>();
        for (MessageRecord messageRecord : messageRecordList) {
            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        for (int i = 0; i < toRemove; i++) {
            Long key = map.keySet().stream().toList().get(0);
            MessageRecord messageRecord = map.get(key);

            map.remove(key);
            this.removeRecord(messageRecord);
        }
    }

}
