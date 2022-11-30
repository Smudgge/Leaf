package com.github.smuddgge.leaf.datatype;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;

public record ProxyServerInterface(ProxyServer proxyServer) {

    /**
     * Used to get a player that is unable to vanish on a server.
     *
     * @param registeredServer The instance of the server.
     * @return The requested player.
     */
    public Player getNotVanishablePlayer(RegisteredServer registeredServer) {
        for (Player player : registeredServer.getPlayersConnected()) {
            User user = new User(player);

            if (!user.isVanishable()) return player;
        }

        return null;
    }

    /**
     * Used to get a filtered list of players.
     * <ul>
     *     <li>Filters players with the permission.</li>
     * </ul>
     *
     * @param permission      The permission to filter.
     * @param includeVanished If the filtered players should
     *                        include vanished players.
     * @return List of filtered players.
     */
    public List<User> getFilteredPlayers(String permission, boolean includeVanished) {
        List<User> players = new ArrayList<>();

        for (Player player : this.proxyServer.getAllPlayers()) {
            User user = new User(player);

            // If the player has the permission node
            if (!user.hasPermission(permission)) continue;

            // If includes vanished players and they are not vanished
            if (!includeVanished && user.isVanished()) continue;

            players.add(user);
        }

        return players;
    }

    /**
     * Used to get a filtered list of players on a server.
     * <ul>
     *     <li>Filters players with the permission.</li>
     * </ul>
     *
     * @param server The instance of a server.
     * @param permission      The permission to filter.
     * @param includeVanished If the filtered players should
     *                        include vanished players.
     * @return List of filtered players.
     */
    public List<User> getFilteredPlayers(RegisteredServer server, String permission, boolean includeVanished) {
        List<User> players = new ArrayList<>();

        for (Player player : server.getPlayersConnected()) {
            User user = new User(player);

            // If the player has the permission node
            if (!user.hasPermission(permission)) continue;

            // If includes vanished players and they are not vanished
            if (!includeVanished && user.isVanished()) continue;

            players.add(user);
        }

        return players;
    }

    /**
     * Used to get a list of registered server names.
     *
     * @return The list of server names.
     */
    public List<String> getServerNames() {
        List<String> servers = new ArrayList<>();

        for (RegisteredServer server : Leaf.getServer().getAllServers()) {
            servers.add(server.getServerInfo().getName());
        }

        return servers;
    }

    /**
     * Used to get a random player from a server.
     *
     * @param server The instance of a server.
     * @return A random player.
     */
    public User getRandomUser(RegisteredServer server) {
        for (Player player : server.getPlayersConnected()) {
            return new User(player);
        }
        return null;
    }
}
