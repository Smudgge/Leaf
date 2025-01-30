package com.github.smuddgge.leaf.helper;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.FriendRecord;
import com.github.smuddgge.leaf.database.FriendTable;
import com.github.smuddgge.leaf.database.PlayerRecord;
import com.github.smuddgge.leaf.database.PlayerTable;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.squishylib.database.Query;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerHelper {

    /**
     * Used to get the list of online players.
     * This will not include vanished players.
     *
     * @return The list of online players.
     */
    public static @NotNull List<String> getPlayers() {
        List<String> players = new ArrayList<>();

        for (Player player : Leaf.get().getProxyServer().getAllPlayers()) {
            PlayerUser user = new PlayerUser(player);

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
    public static @NotNull List<String> getPlayers(@NotNull PlayerUser user) {
        if (user.isNotVanishable()) return PlayerHelper.getPlayers();
        if (Leaf.get().getConfig().canVanishableSeeVanishable()) return PlayerHelper.getPlayersRaw();
        return PlayerHelper.getPlayers();
    }

    /**
     * Used to get every player on the server.
     * Even if the player is in vanish.
     *
     * @return Every player on the server.
     */
    public static @NotNull List<String> getPlayersRaw() {
        List<String> players = new ArrayList<>();

        for (Player player : Leaf.get().getProxyServer().getAllPlayers()) {
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
        if (Leaf.get().isDatabaseDisabled()) return new ArrayList<>();

        List<String> players = new ArrayList<>();

        final List<PlayerRecord> records = Leaf.get().getDatabase().getTable(PlayerTable.class)
                .getRecordList().waitAndGetNotNull();

        // Loop though all player record's.
        for (PlayerRecord playerRecord : records) {
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
    public static @NotNull List<String> getFriends(PlayerUser user) {

        // Check if the database is disabled.
        if (Leaf.get().isDatabaseDisabled()) return new ArrayList<>();

        final List<FriendRecord> friends = Leaf.get().getDatabase().getTable(FriendTable.class)
                .getRecordList(new Query().match(FriendRecord.PLAYER_UUID, user.getUuid()))
                .waitAndGetNotNull();

        List<String> friendNameList = new ArrayList<>();

        for (FriendRecord friendRecord : friends) {
            friendNameList.add(friendRecord.getFriendNameFormatted());
        }

        return friendNameList;
    }

    /**
     * Get a player that is unable to vanish on a specific server.
     *
     * @param server The server.
     * @return A random player that is unable to vanish.
     */
    public static @Nullable Player getNotVanishablePlayer(@NotNull RegisteredServer server) {
        for (final Player player : server.getPlayersConnected()) {
            final PlayerUser user = new PlayerUser(player);

            if (user.isNotVanishable()) return player;
        }

        return null;
    }
}
