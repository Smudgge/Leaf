package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.utility.DateAndTime;

import java.util.UUID;

/**
 * Represents the friend request manager.
 */
public class FriendRequestManager {

    /**
     * Used to send a friend request though the database.
     * This method does not send any extra messages to the player.
     *
     * @param userFrom       The user the request will be from.
     * @param playerRecordTo The player to send the request to.
     */
    public static boolean sendRequest(User userFrom, PlayerRecord playerRecordTo) {
        if (Leaf.getDatabase().isDisabled()) return false;

        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");

        if (friendRequestTable == null) return false;

        FriendRequestRecord requestRecord = new FriendRequestRecord();
        requestRecord.uuid = String.valueOf(UUID.randomUUID());
        requestRecord.dateCreated = DateAndTime.getNow();
        requestRecord.notes = "A friend request!";
        requestRecord.playerFromUuid = userFrom.getUniqueId().toString();
        requestRecord.playerToUuid = playerRecordTo.uuid;

        friendRequestTable.insertRecord(requestRecord);

        return true;
    }
}
