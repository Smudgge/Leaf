package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class Join extends BaseCommandType {

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        // Get the server.
        String server = section.getString("server");

        // Check if the server exists.
        if (server == null) {
            MessageManager.warn("Server is not defined for command : " + section.getString("name"));
            return new CommandStatus().error();
        }

        Optional<RegisteredServer> optionalRegisteredServer = Leaf.getServer().getServer(server);

        if (optionalRegisteredServer.isEmpty()) {
            user.sendMessage(section.getString("no_server", "{error_colour}This server is offline"));
            return new CommandStatus();
        }

        // Create a connection.
        user.send(optionalRegisteredServer.get());
        return new CommandStatus();
    }
}
