package com.github.smuddgge.leaf.datatype;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Represents a user connected to one of the servers.
 */
public class User {

    /**
     * The instance of the player
     */
    private final Player player;

    /**
     * Used to create a user.
     *
     * @param player The player to wrap.
     */
    public User(Player player) {
        this.player = player;
    }

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
    }
}
