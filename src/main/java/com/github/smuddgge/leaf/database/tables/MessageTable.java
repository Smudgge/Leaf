package com.github.smuddgge.leaf.database.tables;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.records.MessageRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.datatype.MessageQuery;
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
     * @param playerUuid1 One of the players.
     * @param playerUuid2 The other player.
     * @return Sorted list of records.
     */
    public List<MessageRecord> getMessagesOrdered(UUID playerUuid1, UUID playerUuid2) {
        Map<Long, MessageRecord> map = new TreeMap<>(Collections.reverseOrder());

        for (MessageRecord messageRecord : this.getRecordList(new Query()
                .match("fromPlayerUuid", playerUuid1.toString())
                .match("toPlayerUuid", playerUuid2.toString()))) {

            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        for (MessageRecord messageRecord : this.getRecordList(new Query()
                .match("fromPlayerUuid", playerUuid2.toString())
                .match("toPlayerUuid", playerUuid1.toString()))) {

            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        ArrayList<MessageRecord> records = new ArrayList<>();
        for (Map.Entry<Long, MessageRecord> entry : map.entrySet()) {
            records.add(entry.getValue());
        }

        return records;
    }

    /**
     * Used to get the records ordered.
     *
     * @param playerUuid One of the players.
     * @return Sorted list of records.
     */
    public List<MessageRecord> getMessagesOrdered(UUID playerUuid) {
        Map<Long, MessageRecord> map = new TreeMap<>(Collections.reverseOrder());

        for (MessageRecord messageRecord : this.getRecordList(new Query()
                .match("fromPlayerUuid", playerUuid.toString()))) {

            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        for (MessageRecord messageRecord : this.getRecordList(new Query()
                .match("toPlayerUuid", playerUuid.toString()))) {

            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        ArrayList<MessageRecord> records = new ArrayList<>();
        for (Map.Entry<Long, MessageRecord> entry : map.entrySet()) {
            records.add(entry.getValue());
        }

        return records;
    }

    /**
     * Used to get the messages ordered.
     *
     * @return All messages ordered.
     */
    public List<MessageRecord> getMessagesOrdered() {
        Map<Long, MessageRecord> map = new TreeMap<>(Collections.reverseOrder());

        for (MessageRecord messageRecord : this.getRecordList()) {
            map.put(Long.parseLong(messageRecord.timeStamp), messageRecord);
        }

        ArrayList<MessageRecord> records = new ArrayList<>();
        for (Map.Entry<Long, MessageRecord> entry : map.entrySet()) {
            records.add(entry.getValue());
        }

        return records;
    }

    /**
     * Used to get message records ordered given a string query.
     *
     * <ul>
     *     <li>p:PlayerName,PlayerName</li>
     *     <li>t:AmountOfTime-AmountOfTime</li>
     *     <li>i:"include messages that contain this string"</li>
     *     <li>e:"exclude messages that contain this string"</li>
     * </ul>
     *
     * @param query The string query.
     * @return List of message records.
     */
    public List<MessageRecord> getMessagesOrdered(String query) {
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        MessageQuery messageQuery = new MessageQuery(query);
        List<MessageRecord> recordList = new ArrayList<>();
        List<MessageRecord> toReturn = new ArrayList<>();

        if (messageQuery.players.size() == 0) {
            recordList.addAll(this.getMessagesOrdered());
        }

        if (messageQuery.players.size() == 1) {
            String name = messageQuery.players.get(0);
            PlayerRecord playerRecord = playerTable.getFirstRecord(new Query().match("name", name));
            assert playerRecord != null;

            recordList.addAll(this.getMessagesOrdered(UUID.fromString(playerRecord.uuid)));
        }

        if (messageQuery.players.size() >= 2) {
            String name1 = messageQuery.players.get(0);
            String name2 = messageQuery.players.get(1);
            PlayerRecord playerRecord1 = playerTable.getFirstRecord(new Query().match("name", name1));
            assert playerRecord1 != null;

            PlayerRecord playerRecord2 = playerTable.getFirstRecord(new Query().match("name", name2));
            assert playerRecord2 != null;

            recordList.addAll(this.getMessagesOrdered(UUID.fromString(playerRecord1.uuid), UUID.fromString(playerRecord2.uuid)));
        }

        for (MessageRecord messageRecord : recordList) {
            long timeStamp = Long.parseLong(messageRecord.timeStamp);
            if (messageQuery.fromTimeStamp != null && timeStamp < messageQuery.fromTimeStamp) continue;
            if (messageQuery.toTimeStamp != null && timeStamp > messageQuery.toTimeStamp) continue;

            boolean includesFlag = true;
            for (String include : messageQuery.include) {
                includesFlag = false;
                if (messageRecord.message.contains(include)) {
                    includesFlag = true;
                    break;
                }
            }

            if (!includesFlag) continue;

            boolean excludesFlag = true;
            for (String exclude : messageQuery.exclude) {
                if (messageRecord.message.contains(exclude)) {
                    excludesFlag = false;
                    break;
                }
            }

            if (!excludesFlag) continue;

            toReturn.add(messageRecord);
        }

        return toReturn;
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
