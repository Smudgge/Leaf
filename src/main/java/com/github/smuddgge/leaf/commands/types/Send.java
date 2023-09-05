package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <h1>Send Command Type</h1>
 * Used to send a selection of players to a
 * certain server.
 */
public class Send extends BaseCommandType {

    @Override
    public String getName() {
        return "send";
    }

    @Override
    public String getSyntax() {
        return "/[name] [from] [to]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        // Get the list of online servers.
        List<String> servers = new ArrayList<>();
        for (RegisteredServer server : Leaf.getServer().getAllServers()) {
            servers.add(server.getServerInfo().getName());
        }

        // Get the list of online players.
        List<String> players = new ArrayList<>();
        for (Player player : Leaf.getServer().getAllPlayers()) {
            User userToAdd = new User(player);
            if (userToAdd.isVanished()) continue;
            players.add(userToAdd.getName());
        }

        // Create the selection list.
        List<String> from = new ArrayList<>();
        from.add("all");
        from.addAll(servers);
        from.addAll(players);

        // Create the command suggestions.
        return new CommandSuggestions().append(from).append(servers);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Get the second argument, the server to send the players to.
        Optional<RegisteredServer> server = Leaf.getServer().getServer(arguments[1]);

        // Check if the server exists.
        if (server.isEmpty()) {
            MessageManager.log(section.getAdaptedString("server_not_found", "\n", "{error_colour}Server not found."));
            return new CommandStatus();
        }

        // Check if the server is online
        try {
            server.get().ping().get();
        } catch (InterruptedException | ExecutionException ignored) {
            MessageManager.log(section.getAdaptedString("server_offline", "\n", "{error_colour}Server requested is offline."));
            return new CommandStatus();
        }

        // Send all the players.
        // Get the amount of players that were sent.
        int amount = this.send(arguments[0], server.get());

        // Log the message in console.
        MessageManager.log(section.getAdaptedString("message", "\n")
                .replace("%from%", arguments[0])
                .replace("%to%", arguments[1])
                .replace("%amount%", String.valueOf(amount)));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Get the second argument, the server to send the players to.
        Optional<RegisteredServer> server = Leaf.getServer().getServer(arguments[1]);

        // Check if the server exists.
        if (server.isEmpty()) {
            user.sendMessage(section.getAdaptedString("server_not_found", "\n", "{error_colour}Server not found."));
            return new CommandStatus();
        }

        // Check if the server is online.
        try {
            server.get().ping().get();
        } catch (InterruptedException | ExecutionException ignored) {
            user.sendMessage(section.getAdaptedString("server_offline", "\n", "{error_colour}Server requested is offline."));
            return new CommandStatus();
        }

        // Send all the players.
        // Get the amount of players that were sent.
        int amount = this.send(arguments[0], server.get());

        // Send the message.
        user.sendMessage(section.getAdaptedString("message", "\n")
                .replace("%from%", arguments[0])
                .replace("%to%", arguments[1])
                .replace("%amount%", String.valueOf(amount)));

        return new CommandStatus();
    }

    /**
     * Used to send players to a server.
     *
     * @param from Which players to send.
     * @param to   The server to send the players to.
     * @return The number of players that were sent.
     */
    private int send(String from, RegisteredServer to) {
        // The final list of players to send.
        List<Player> playersToSend = new ArrayList<>();

        // Check if all players should be sent.
        if (Objects.equals(from, "all")) {
            playersToSend.addAll(Leaf.getServer().getAllPlayers());
        }

        // Check if the argument was a server.
        if (Leaf.getServer().getServer(from).isPresent()) {
            playersToSend.addAll(Leaf.getServer().getServer(from).get().getPlayersConnected());
        }

        // Check if the argument was a player.
        Optional<Player> playerFrom = Leaf.getServer().getPlayer(from);
        if (playersToSend.size() == 0 && playerFrom.isPresent()) {
            playersToSend.add(playerFrom.get());
        }

        // Check if no players will be sent.
        if (playersToSend.size() == 0) return 0;

        // Represents the amount of player that gets sent.
        int amount = 0;

        for (Player player : playersToSend) {

            try {
                // Send the player to the destination.
                CompletableFuture<Boolean> result = player.createConnectionRequest(to).connectWithIndication();

                // Get the result.
                if (result.isCancelled()) continue;
                if (result.get()) amount += 1;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return amount;
    }
}
