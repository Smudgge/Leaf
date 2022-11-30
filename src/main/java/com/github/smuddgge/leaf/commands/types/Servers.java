package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.FakeUser;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the server command type.
 */
public class Servers implements CommandType {

    @Override
    public String getName() {
        return "servers";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {

        MessageManager.log(this.getMessage(section));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        user.sendMessage(this.getMessage(section));

        return new CommandStatus();
    }

    private String getMessage(ConfigurationSection section) {
        ProxyServerInterface proxyServerInterface = new ProxyServerInterface(Leaf.getServer());
        StringBuilder builder = new StringBuilder();

        builder.append(section.getString("header")).append("&r\n\n");

        List<String> order = section.getListString("order", new ArrayList<>());

        for (String serverName : order) {

            Optional<RegisteredServer> server = Leaf.getServer().getServer(serverName);

            if (server.isEmpty()) continue;

            String parsed = PlaceholderManager.parse(section.getString("server"), null, new FakeUser(server.get()));

            int online = proxyServerInterface.getFilteredPlayers(server.get(), null, false).size();

            builder.append(parsed.replace("%online%", String.valueOf(online)));
            builder.append("&r\n");
        }

        builder.append("&r\n");
        builder.append(section.getString("footer"));

        return builder.toString();
    }
}
