package com.github.smuddgge.leaf.datatype;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Represents a user connected to one of the servers.
 */
public record User(Player player) {

    /**
     * Used to get what server the user is connected to.
     *
     * @return The registered server.
     */
    public RegisteredServer getConnectedServer() {
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
        return this.player.hasPermission(permission);
    }

    /**
     * Used to check if a user is vanished.
     *
     * @return True if they are vanished.
     */
    public boolean isVanished() {
        // If they are unable to vanish return false
        if (!this.isVanishable()) return false;

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
    public boolean isVanishable() {
        return this.hasPermission(ConfigCommands.getVanishablePermission());
    }

    /**
     * Used to send a user a message.
     * This will also convert the messages placeholders and colours.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        this.player.sendMessage(MessageManager.convert(message, this));
    }

    /**
     * Used to get the users name.
     *
     * @return The users name.
     */
    public CharSequence getName() {
        return this.player.getGameProfile().getName();
    }
}
