package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.configuration.ConfigurationManager;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.FriendRequestRecord;
import com.github.smuddgge.leaf.database.records.FriendSettingsRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendRequestTable;
import com.github.smuddgge.leaf.database.tables.FriendSettingsTable;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.DateAndTime;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import com.github.smuddgge.squishydatabase.console.Console;
import com.velocitypowered.api.proxy.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the friend request manager.
 */
public class FriendManager {

    /**
     * Used to send a friend request though the database.
     * This method does not send any extra messages to the player.
     *
     * @param userFrom       The user the request will be from.
     * @param playerRecordTo The player to send the request to.
     */
    public static boolean sendRequest(User userFrom, PlayerRecord playerRecordTo) {
        if (Leaf.isDatabaseDisabled()) return false;

        FriendRequestTable friendRequestTable = Leaf.getDatabase().getTable(FriendRequestTable.class);

        FriendRequestRecord requestRecord = new FriendRequestRecord();
        requestRecord.uuid = String.valueOf(UUID.randomUUID());
        requestRecord.dateCreated = DateAndTime.getNow();
        requestRecord.notes = "A friend request!";
        requestRecord.playerFromUuid = userFrom.getUniqueId().toString();
        requestRecord.playerToUuid = playerRecordTo.uuid;

        friendRequestTable.insertRecord(requestRecord);
        return true;
    }

    /**
     * Used to accept a friend request.
     *
     * @param requestRecord The instance of the request record.
     */
    public static void acceptRequest(FriendRequestRecord requestRecord) {
        if (Leaf.isDatabaseDisabled()) return;

        FriendRequestTable friendRequestTable = Leaf.getDatabase().getTable(FriendRequestTable.class);
        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

        PlayerRecord playerFrom = playerTable.getFirstRecord(new Query().match("uuid", requestRecord.playerFromUuid));
        PlayerRecord playerTo = playerTable.getFirstRecord(new Query().match("uuid", requestRecord.playerToUuid));
        assert playerTo != null;
        assert playerFrom != null;

        friendRequestTable.removeRecord(requestRecord);

        FriendRecord friendRecord = new FriendRecord();
        friendRecord.uuid = UUID.randomUUID().toString();
        friendRecord.timeStampCreated = String.valueOf(System.currentTimeMillis());
        friendRecord.dateCreated = DateAndTime.getNow();
        friendRecord.staredBoolean = "false";
        friendRecord.toggleProxyJoin = "false";
        friendRecord.toggleProxyLeave = "false";
        friendRecord.toggleServerChange = "false";
        friendRecord.friendNameFormatted = playerTo.name;
        friendRecord.playerUuid = requestRecord.playerFromUuid;
        friendRecord.friendPlayerUuid = requestRecord.playerToUuid;

        friendTable.insertRecord(friendRecord);

        FriendRecord friendRecord2 = new FriendRecord();
        friendRecord2.uuid = UUID.randomUUID().toString();
        friendRecord2.timeStampCreated = friendRecord.timeStampCreated;
        friendRecord2.dateCreated = friendRecord.dateCreated;
        friendRecord2.staredBoolean = "false";
        friendRecord2.toggleProxyJoin = "false";
        friendRecord2.toggleProxyLeave = "false";
        friendRecord2.toggleServerChange = "false";
        friendRecord2.friendNameFormatted = playerFrom.name;
        friendRecord2.playerUuid = requestRecord.playerToUuid;
        friendRecord2.friendPlayerUuid = requestRecord.playerFromUuid;

        friendTable.insertRecord(friendRecord2);
    }

    /**
     * Used to unfriend a player.
     *
     * @param playerUuid The players uuid.
     * @param friendUuid THe friends uuid.
     */
    public static void unFriend(String playerUuid, String friendUuid) {
        if (Leaf.isDatabaseDisabled()) return;

        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        FriendRecord friendRecord1 = friendTable.getFriend(playerUuid, friendUuid);
        FriendRecord friendRecord2 = friendTable.getFriend(friendUuid, playerUuid);
        assert friendRecord1 != null;
        assert friendRecord2 != null;

        friendTable.removeRecord(friendRecord1);
        friendTable.removeRecord(friendRecord2);
    }

    /**
     * Used to check if a player has already requested a player.
     *
     * @param playerFrom Player uuid the request was sent from.
     * @param playerTo   Player uuid the request was sent to.
     * @return True if they have already requested.
     */
    public static boolean hasRequested(UUID playerFrom, String playerTo) {
        FriendRequestTable friendRequestTable = Leaf.getDatabase().getTable(FriendRequestTable.class);

        int amount = friendRequestTable.getAmountOfRecords(new Query()
                .match("playerFromUuid", playerFrom.toString())
                .match("playerToUuid", playerTo)
        );

        return amount > 0;
    }

    /**
     * Used to check if two players are friends.
     *
     * @param uuid1 The first players uuid.
     * @param uuid2 The second players uuid.
     * @return True if they are friends.
     */
    public static boolean isFriends(UUID uuid1, UUID uuid2) {
        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        FriendRecord record = friendTable.getFriend(uuid1.toString(), uuid2.toString());
        return record != null;
    }

    /**
     * Called when a user joins the proxy and is able to be seen.
     *
     * @param user The instance of the user.
     */
    public static void onProxyJoin(User user) {
        // Check if the friend command is enabled.
        ConfigurationSection section = ConfigurationManager.getCommands().getCommandFromType("friends");
        if (section == null) return;

        if (Leaf.isDatabaseDisabled()) return;

        // Get the tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        FriendSettingsTable friendSettingsTable = Leaf.getDatabase().getTable(FriendSettingsTable.class);

        // Get the message.
        String message = section.getString("proxy_join", "&8[&a+&8] &7Your friend &a<player> &7joined {server_formatted}");

        // Loops though each of the players friends.
        for (FriendRecord friendRecord : friendTable.getFriendList(user.getUniqueId().toString())) {

            // Get the player's name.
            String friendUuid = friendRecord.friendPlayerUuid;
            PlayerRecord friendPlayerRecord = playerTable.getFirstRecord(new Query().match("uuid", friendUuid));
            if (friendPlayerRecord == null) {
                Console.warn("Could not find player in player table when sending friend event!");
                continue;
            }

            // Get the friend's name.
            String friendName = friendPlayerRecord.name;

            // Attempt to get if they are on the server.
            Optional<Player> optional = Leaf.getServer().getPlayer(friendName);
            if (optional.isEmpty()) continue;
            Player player = optional.get();

            // Get there friend settings.
            FriendSettingsRecord settings = friendSettingsTable.getSettings(friendUuid);

            // Check if they have settings toggled.
            if (Objects.equals(settings.toggleProxyJoin, "false")) continue;

            // Send the message.
            new User(player).sendMessage(PlaceholderManager.parse(message, null, user));
        }
    }

    /**
     * Called when a user leaves the proxy and is able to be seen.
     *
     * @param user The instance of the user.
     */
    public static void onProxyLeave(User user) {
        ConfigurationSection section = ConfigurationManager.getCommands().getCommandFromType("friends");

        if (section == null) return;

        // Get the tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        FriendSettingsTable friendSettingsTable = Leaf.getDatabase().getTable(FriendSettingsTable.class);

        String message = section.getString("proxy_leave", "&8[&c-&8] &7Your friend &c<player> &7left the network");

        for (FriendRecord friendRecord : friendTable.getFriendList(user.getUniqueId().toString())) {

            // Get the player's name.
            String friendUuid = friendRecord.friendPlayerUuid;
            PlayerRecord friendPlayerRecord = playerTable.getFirstRecord(new Query().match("uuid", friendUuid));
            if (friendPlayerRecord == null) {
                Console.warn("Could not find player in player table when sending friend event!");
                continue;
            }

            // Get the friend's name.
            String friendName = friendPlayerRecord.name;

            Optional<Player> optional = Leaf.getServer().getPlayer(friendName);
            if (optional.isEmpty()) continue;
            Player player = optional.get();

            FriendSettingsRecord settings = friendSettingsTable.getSettings(friendUuid);

            if (Objects.equals(settings.toggleProxyLeave, "false")) continue;

            new User(player).sendMessage(PlaceholderManager.parse(message, null, user));
        }
    }

    /**
     * Called when a user changes server and is able to be seen.
     *
     * @param user The instance of the user.
     */
    public static void onChangeServer(User user) {
        ConfigurationSection section = ConfigurationManager.getCommands().getCommandFromType("friends");

        if (section == null) return;

        // Get the tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        FriendSettingsTable friendSettingsTable = Leaf.getDatabase().getTable(FriendSettingsTable.class);

        String message = section.getString("server_change", "&8[&e=&8] &7Your friend &e<player> &7switched to {server_formatted}");

        for (FriendRecord friendRecord : friendTable.getFriendList(user.getUniqueId().toString())) {

            // Get the player's name.
            String friendUuid = friendRecord.friendPlayerUuid;
            PlayerRecord friendPlayerRecord = playerTable.getFirstRecord(new Query().match("uuid", friendUuid));
            if (friendPlayerRecord == null) {
                Console.warn("Could not find player in player table when sending friend event!");
                continue;
            }

            // Get the friend's name.
            String friendName = friendPlayerRecord.name;

            Optional<Player> optional = Leaf.getServer().getPlayer(friendName);
            if (optional.isEmpty()) continue;
            Player player = optional.get();

            FriendSettingsRecord settings = friendSettingsTable.getSettings(friendUuid);

            if (Objects.equals(settings.toggleServerChange, "false")) continue;

            new User(player).sendMessage(PlaceholderManager.parse(message, null, user));
        }
    }
}
