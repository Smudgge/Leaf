package com.github.smuddgge.leaf.utility;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player utility class.
 * Used to get lists of certain players.
 */
public class PlayerUtility {

    /**
     * Used to get the list of online players.
     * This will not include vanished players.
     *
     * @return The list of online players.
     */
    public static @NotNull List<String> getPlayers() {
        List<String> players = new ArrayList<>();

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User user = new User(player);

            if (user.isVanished()) continue;

            players.add(player.getGameProfile().getName());
        }

        return players;
    }

    /**
     * Used to get the list of online players.
     * If vanishable players are able to see vanishable player's this
     * will also be checked in this method.
     *
     * @param user The user to check if they are able to vanish.
     * @return The list of players.
     */
    public static @NotNull List<String> getPlayers(@NotNull User user) {
        if (user.isNotVanishable()) return PlayerUtility.getPlayers();
        if (ConfigMain.getVanishableCanSeeVanishable()) return PlayerUtility.getPlayersRaw();
        return PlayerUtility.getPlayers();
    }

    /**
     * Used to get every player on the server.
     * Even if the player is in vanish.
     *
     * @return Every player on the server.
     */
    public static @NotNull List<String> getPlayersRaw() {
        List<String> players = new ArrayList<>();

        for (Player player : Leaf.getServer().getAllPlayers()) {
            players.add(player.getGameProfile().getName());
        }

        return players;
    }

    /**
     * Used to get all the players that are
     * registered in the database.
     *
     * @return The list of database players.
     */
    public static @NotNull List<String> getDatabasePlayers() {
        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return new ArrayList<>();

        List<String> players = new ArrayList<>();

        // Loop though all player record's.
        for (PlayerRecord playerRecord : Leaf.getDatabase().getTable(PlayerTable.class).getRecordList()) {
            players.add(playerRecord.name);
        }

        return players;
    }

    /**
     * Used to get a user's list of friends.
     *
     * @param user The instance of the user.
     * @return The list of friends.
     */
    public static @NotNull List<String> getFriends(User user) {
        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return new ArrayList<>();

        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);
        List<FriendRecord> friends = friendTable.getRecordList(new Query().match("playerUuid", user.getUniqueId()));

        List<String> friendNameList = new ArrayList<>();
        for (FriendRecord friendRecord : friends) {
            friendNameList.add(friendRecord.friendNameFormatted);
        }

        return friendNameList;
    }
}
