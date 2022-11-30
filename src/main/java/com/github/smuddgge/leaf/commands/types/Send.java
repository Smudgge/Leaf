package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the send command type.
 */
public class Send implements CommandType {

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
        if (arguments.length >= 3) return new CommandStatus().incorrectArguments();

        boolean success = this.send(arguments[0], arguments[1]);

        if (success) {
            MessageManager.log(section.getString("message")
                    .replace("%from%", arguments[0])
                    .replace("%to%", arguments[1]));

            return new CommandStatus();
        }

        return new CommandStatus().incorrectArguments();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length >= 3) return new CommandStatus().incorrectArguments();

        boolean success = this.send(arguments[0], arguments[1]);

        if (success) {
            user.sendMessage(section.getString("message")
                    .replace("%from%", arguments[0])
                    .replace("%to%", arguments[1]));

            return new CommandStatus();
        }

        return new CommandStatus().incorrectArguments();
    }

    private boolean send(String from, String to) {
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

        if (playersToSend.size() == 0) return false;

        Optional<RegisteredServer> server = Leaf.getServer().getServer(to);

        if (server.isEmpty()) return false;

        for (Player player : playersToSend) {
            player.createConnectionRequest(server.get());
        }

        return true;
    }
}
