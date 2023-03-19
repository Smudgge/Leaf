package com.github.smuddgge.leaf.datatype;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.github.smuddgge.leaf.database.records.IgnoreRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.HistoryTable;
import com.github.smuddgge.leaf.database.tables.IgnoreTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.listeners.PlayerHistoryEventType;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * <h1>Represents a user connected to one of the servers.</h1>
 * Adds more options onto the player class.
 * Can also represent a fake player.
 */
public class User {

    private final Player player;
    private RegisteredServer server;
    private final String name;

    /**
     * Used to create a user.
     *
     * @param player The player instance.
     */
    public User(Player player) {
        this.player = player;
        this.server = null;
        this.name = null;
    }

    /**
     * Used to create a user.
     *
     * @param server The server the user is connected to.
     * @param name   The name of the user.
     */
    public User(RegisteredServer server, String name) {
        this.player = null;
        this.server = server;
        this.name = name;
    }

    /**
     * Used to get the users name.
     *
     * @return The users name.
     */
    public String getName() {
        if (this.player == null) return this.name;
        return this.player.getGameProfile().getName();
    }

    /**
     * Used to get the users unique id.
     *
     * @return The users unique id.
     */
    public UUID getUniqueId() {
        if (player == null) return null;
        return player.getUniqueId();
    }

    /**
     * Used to get what server the user is connected to.
     *
     * @return The registered server.
     */
    public RegisteredServer getConnectedServer() {
        if (this.server != null) return this.server;
        if (this.player == null) return this.server;
        if (this.player.getCurrentServer().isEmpty()) return null;

        return this.player.getCurrentServer().get().getServer();
    }

    /**
     * Used to get the players record from the database.
     *
     * @return The players record.
     */
    public PlayerRecord getRecord() {
        if (this.player == null) return null;
        if (Leaf.isDatabaseDisabled()) return null;

        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        return playerTable.getFirstRecord(new Query().match("uuid", this.getUniqueId().toString()));
    }

    /**
     * Used to get who this user last messaged.
     *
     * @return Instance of a user this user last messaged.
     * Return null if the player doesn't exist.
     */
    public User getLastMessaged() {
        if (this.player == null) return null;
        if (this.player.getUniqueId() == null) return null;

        UUID uuidLastMessaged = MessageManager.getLastMessaged(this.player.getUniqueId());

        if (uuidLastMessaged == null) return null;

        Optional<Player> request = Leaf.getServer().getPlayer(uuidLastMessaged);

        if (request.isEmpty()) {
            MessageManager.removeLastMessaged(player.getUniqueId());
            return null;
        }

        return new User(request.get());
    }

    /**
     * Used to get the highest permission this user
     * has in a list of permissions.
     *
     * @param permissions Permissions to check.
     * @return The highest permission.
     */
    public String getHighestPermission(List<String> permissions) {
        if (this.player == null) return null;
        for (String permission : permissions) {
            if (this.player.hasPermission(permission)) return permission;
        }
        return null;
    }

    /**
     * Used to set the registered server.
     *
     * @param server The registered server.
     */
    public void setConnectedServer(RegisteredServer server) {
        this.server = server;
    }

    /**
     * Used to check if the user has a permission.
     *
     * @param permission Permission to check for.
     * @return True if they have the permission.
     */
    public boolean hasPermission(String permission) {
        if (this.player == null) return true;
        if (permission == null) return true;

        return this.player.hasPermission(permission);
    }

    /**
     * Used to check if the user has there messages toggled.
     *
     * @return True if they can message.
     */
    public boolean canMessage() {
        if (Leaf.isDatabaseDisabled()) return true;
        if (this.player == null) return true;
        if (this.getRecord().toggleCanMessage == null) return true;
        return Boolean.parseBoolean(this.getRecord().toggleCanMessage);
    }

    /**
     * Used to check if a user is vanished.
     *
     * @return True if they are vanished.
     */
    public boolean isVanished() {
        if (this.player == null) return false;

        // If they are unable to vanish return false
        if (this.isNotVanishable()) return false;

        ProxyServerInterface proxyServerInterface = new ProxyServerInterface(Leaf.getServer());
        RegisteredServer server = this.getConnectedServer();

        // If they are not connected to a server they are vanished
        if (server == null) return true;

        Player unableToVanishPlayer = proxyServerInterface.getNotVanishablePlayer(server);

        // If there are no players online that can not vanish
        // we assume they are vanished.
        if (unableToVanishPlayer == null) return true;

        // Check if this player can be seen on the tab list by
        // players that can not vanish.
        return !unableToVanishPlayer.getTabList().containsEntry(this.player.getUniqueId());
    }

    /**
     * Used to check if a user is able to vanish.
     *
     * @return True if the player is able to vanish.
     */
    public boolean isNotVanishable() {
        return !this.hasPermission(ConfigMain.getVanishablePermission());
    }

    /**
     * Used to check if a user is ignoring another player.
     *
     * @param uuid The other players uuid.
     * @return True if they are ignoring this player.
     */
    public boolean isIgnoring(UUID uuid) {
        if (Leaf.isDatabaseDisabled()) return false;

        IgnoreTable ignoreTable = Leaf.getDatabase().getTable(IgnoreTable.class);

        // Check all records.
        for (IgnoreRecord ignoreRecord : ignoreTable.getRecordList(
                new Query().match("playerUuid", this.getUniqueId())
        )) {
            if (Objects.equals(ignoreRecord.ignoredPlayerUuid, uuid.toString())) return true;
        }

        return false;
    }

    /**
     * Used to send a user a message.
     * This will also convert the messages placeholders and colours.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        if (this.player == null) return;
        this.player.sendMessage(MessageManager.convert(message, this));
    }

    /**
     * Used to update this user in the database.
     */
    public void updateDatabase() {
        if (Leaf.isDatabaseDisabled()) return;
        Leaf.getDatabase().getTable(PlayerTable.class).updatePlayer(this);
    }

    /**
     * Used to add player history to the database.
     *
     * @param server                 The server to append.
     * @param playerHistoryEventType The type of history to append.
     */
    public void addHistory(RegisteredServer server, PlayerHistoryEventType playerHistoryEventType) {
        if (Leaf.isDatabaseDisabled()) return;

        HistoryTable historyTable = Leaf.getDatabase().getTable(HistoryTable.class);

        historyTable.insertHistory(
                this.getUniqueId().toString(),
                server.getServerInfo().getName(),
                playerHistoryEventType
        );
    }

    /**
     * Used to teleport the player to a registered server.
     *
     * @param connectedServer The server to teleport them to.
     * @return If it was successful.
     */
    public boolean teleport(RegisteredServer connectedServer) {
        if (this.player == null) return false;
        this.player.createConnectionRequest(connectedServer).connectWithIndication();
        return true;
    }

    /**
     * Used to send the user to a server.
     *
     * @param server The server to send to.
     */
    public void send(RegisteredServer server) {
        if (this.player == null) return;
        if (this.server == null) return;
        try {
            this.player.createConnectionRequest(server);
        } catch (Exception ignored) {
        }
    }

    /**
     * Used to attempt to send a user to a server in a list.
     * This will use load balancing to decide which server to send to.
     *
     * @param servers The list of server names.
     */
    public void send(List<String> servers) {
        // Check if the server exists.
        if (servers.size() == 0) return;

        // Set default values.
        RegisteredServer server = null;
        int amountOfPlayers = 0;

        for (String tempServer : servers) {
            Optional<RegisteredServer> optionalTempServer = Leaf.getServer().getServer(tempServer);
            if (optionalTempServer.isEmpty()) continue;

            // Get the amount of players on the server.
            int size = optionalTempServer.get().getPlayersConnected().size();

            // If the amount of players is still 0 set this to be the server.
            if (amountOfPlayers == 0) {
                server = optionalTempServer.get();
                amountOfPlayers = size;
                continue;
            }

            // If the size is bigger then the current the smallest server.
            if (size > amountOfPlayers) continue;

            server = optionalTempServer.get();
            amountOfPlayers = size;
        }

        this.send(server);
    }

    /**
     * Used to execute a command in this plugin.
     *
     * @param command The command to execute.
     */
    public void executeCommand(String command) {
        if (this.player == null) return;
        Leaf.getCommandHandler().execute(this.player, command);
    }
}
