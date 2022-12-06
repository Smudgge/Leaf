package com.github.smuddgge.leaf.datatype;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a user connected to one of the servers.
 */
public class User {

    private final Player player;
    private final RegisteredServer server;
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
     * Used to get what server the user is connected to.
     *
     * @return The registered server.
     */
    public RegisteredServer getConnectedServer() {
        if (this.player == null) return this.server;
        if (this.player.getCurrentServer().isEmpty()) return null;

        return this.player.getCurrentServer().get().getServer();
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
        return !this.hasPermission(ConfigCommands.getVanishablePermission());
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
     * Used to get the users name.
     *
     * @return The users name.
     */
    public String getName() {
        if (this.player == null) return this.name;

        return this.player.getGameProfile().getName();
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
     * Used to get the users unique id.
     *
     * @return The users unique id.
     */
    public UUID getUniqueId() {
        if (player == null) return null;
        return player.getUniqueId();
    }
}
