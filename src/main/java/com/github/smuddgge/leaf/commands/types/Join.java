package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;
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
        String server = section.getString("server", null);

        // If the server is not a string.
        if (server == null) {

            List<String> listOfServers = section.getListString("server", new ArrayList<>());

            // Check if the server exists.
            if (listOfServers.size() == 0) {
                MessageManager.warn("Server is not defined for command : " + section.getString("name"));
                return new CommandStatus().error();
            }

            // Set default value.
            server = listOfServers.get(0);

            // Get the server with the least amount of players.
            int amountOfPlayers = 0;
            for (String tempServer : listOfServers) {
                Optional<RegisteredServer> optionalTempServer = Leaf.getServer().getServer(tempServer);
                if (optionalTempServer.isEmpty()) continue;

                // Get the amount of players on the server.
                int size = optionalTempServer.get().getPlayersConnected().size();

                // If the amount of players is still 0 set this to be the server.
                if (amountOfPlayers == 0) {
                    server = tempServer;
                    amountOfPlayers = size;
                    continue;
                }

                // If the size is bigger then the current the smallest server.
                if (size > amountOfPlayers) continue;

                server = tempServer;
                amountOfPlayers = size;
            }
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
