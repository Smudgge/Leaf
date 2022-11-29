package com.github.smuddgge.leaf.datatype;

import com.github.smuddgge.leaf.MessageManager;
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
        // TODO: check if the user is vanished
        return false;
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
