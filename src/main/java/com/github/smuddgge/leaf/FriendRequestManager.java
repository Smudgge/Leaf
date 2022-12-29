package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.utility.DateAndTime;

import java.util.Date;
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

        FriendRequestRecord requestRecord = new FriendRequestRecord();
        requestRecord.uuid = String.valueOf(UUID.randomUUID());
        requestRecord.dateCreated = DateAndTime.getNow();
        requestRecord.notes = "A friend request!";
        requestRecord.playerFromUuid = userFrom.getUniqueId().toString();
        requestRecord.playerToUuid = playerRecordTo.uuid;

        friendRequestTable.insertRecord(requestRecord);

        return true;
    }

    public static void acceptRequest(FriendRequestRecord requestRecord) {
        if (Leaf.getDatabase().isDisabled()) return;

        FriendRequestTable friendRequestTable = (FriendRequestTable) Leaf.getDatabase().getTable("FriendRequest");
        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");

        friendRequestTable.removeRecord("uuid", requestRecord.uuid);

        FriendRecord friendRecord = new FriendRecord();
        friendRecord.uuid = UUID.randomUUID().toString();
        friendRecord.timeStampCreated = String.valueOf(System.currentTimeMillis());
        friendRecord.dateCreated = DateAndTime.getNow();
        friendRecord.toggleProxyJoin = "false";
        friendRecord.toggleProxyLeave = "false";
        friendRecord.toggleServerChange = "false";
        friendRecord.playerUuid = requestRecord.playerFromUuid;
        friendRecord.friendPlayerUuid = requestRecord.playerToUuid;

        friendTable.insertRecord(friendRecord);

        FriendRecord friendRecord2 = new FriendRecord();
        friendRecord2.uuid = UUID.randomUUID().toString();
        friendRecord2.timeStampCreated = friendRecord.timeStampCreated;
        friendRecord2.dateCreated = friendRecord.dateCreated;
        friendRecord2.toggleProxyJoin = "false";
        friendRecord2.toggleProxyLeave = "false";
        friendRecord2.toggleServerChange = "false";
        friendRecord2.playerUuid = requestRecord.playerToUuid;
        friendRecord2.friendPlayerUuid = requestRecord.playerFromUuid;

        friendTable.insertRecord(friendRecord2);
    }
}
