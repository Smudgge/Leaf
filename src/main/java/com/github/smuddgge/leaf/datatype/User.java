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
}
