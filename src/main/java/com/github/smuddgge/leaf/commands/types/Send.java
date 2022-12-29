package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Represents the send command type.
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
    public CommandSuggestions getSuggestions(User user) {
        List<String> servers = new ArrayList<>();
        for (RegisteredServer server : Leaf.getServer().getAllServers()) {
            servers.add(server.getServerInfo().getName());
        }

        List<String> players = new ArrayList<>();
        for (Player player : Leaf.getServer().getAllPlayers()) {
            players.add(player.getGameProfile().getName());
        }

        List<String> from = new ArrayList<>();
        from.add("all");
        from.addAll(servers);
        from.addAll(players);

        CommandSuggestions suggestions = new CommandSuggestions();

        suggestions.append(from);
        suggestions.append(servers);

        return suggestions;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        Optional<RegisteredServer> server = Leaf.getServer().getServer(arguments[1]);

        // Check if the server exists
        if (server.isEmpty()) {
            MessageManager.log(section.getString("server_not_found", "{error_colour}Server not found."));
            return new CommandStatus();
        }

        // Check if the server is online
        try {
            server.get().ping().get();
        } catch (InterruptedException | ExecutionException e) {
            MessageManager.log(section.getString("server_offline", "{error_colour}Server requested is offline."));
            return new CommandStatus();
        }

        int amount = this.send(arguments[0], server.get());

        MessageManager.log(section.getString("message")
                .replace("%from%", arguments[0])
                .replace("%to%", arguments[1])
                .replace("%amount%", String.valueOf(amount)));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        Optional<RegisteredServer> server = Leaf.getServer().getServer(arguments[1]);

        // Check if the server exists
        if (server.isEmpty()) {
            user.sendMessage(section.getString("server_not_found", "{error_colour}Server not found."));
            return new CommandStatus();
        }

        // Check if the server is online
        try {
            server.get().ping().get();
        } catch (InterruptedException | ExecutionException e) {
            user.sendMessage(section.getString("server_offline", "{error_colour}Server requested is offline."));
            return new CommandStatus();
        }

        int amount = this.send(arguments[0], server.get());

        user.sendMessage(section.getString("message")
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
     * @return How many players were sent.
     */
    private int send(String from, RegisteredServer to) {
        List<Player> playersToSend = new ArrayList<>();

        if (Objects.equals(from, "all")) {
            playersToSend.addAll(Leaf.getServer().getAllPlayers());
        }

        if (Leaf.getServer().getServer(from).isPresent()) {
            playersToSend.addAll(Leaf.getServer().getServer(from).get().getPlayersConnected());
        }

        Optional<Player> playerFrom = Leaf.getServer().getPlayer(from);
        if (playersToSend.size() == 0 && playerFrom.isPresent()) {
            playersToSend.add(playerFrom.get());
        }

        if (playersToSend.size() == 0) return 0;

        int amount = 0;

        for (Player player : playersToSend) {
            CompletableFuture<Boolean> result = player.createConnectionRequest(to).connectWithIndication();

            try {
                if (result.get()) amount += 1;
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }

        return amount;
    }
}
