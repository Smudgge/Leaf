package com.github.smuddgge.leaf.events;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Manages proxy events for this plugin.
 */
public class EventManager {

    /**
     * Executed before a player connects to a server.
     *
     * @param event Post connection event.
     */
    public static void onPlayerJoin(ServerPostConnectEvent event) {
        // Check if we are connected to the database
        if (Leaf.getDatabase() == null || Leaf.getDatabase().isDisabled()) return;

        // Get the user
        User user = new User(event.getPlayer());

        // Update the player in the database
        user.updateDatabase();

        // Get server connecting to
        RegisteredServer server = user.getConnectedServer();

        // Add history
        user.addHistory(server, PlayerHistoryEventType.JOIN);
    }

    /**
     * Executed when a player disconnects from a server.
     *
     * @param event Disconnection event.
     */
    public static void onPlayerLeave(DisconnectEvent event) {
        // Check if we are connected to the database
        if (Leaf.getDatabase() == null || Leaf.getDatabase().isDisabled()) return;

        // Get the user
        User user = new User(event.getPlayer());

        // Update the player in the database
        user.updateDatabase();

        // Get server connecting to
        RegisteredServer server = user.getConnectedServer();

        // Add history
        user.addHistory(server, PlayerHistoryEventType.LEAVE);
    }
}
