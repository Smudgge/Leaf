package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.ProxyServerInterface;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h1>Servers Command Type</h1>
 * Used to get the list of online servers
 * and the servers information.
 */
public class Servers extends BaseCommandType {

    @Override
    public String getName() {
        return "servers";
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
        // Log the message.
        MessageManager.log(this.getMessage(section));
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        // Send the message to the user.
        user.sendMessage(this.getMessage(section));
        return new CommandStatus();
    }

    /**
     * Used to get the servers message.
     *
     * @param section The instance of the configuration section.
     * @return The requested message.
     */
    private String getMessage(ConfigurationSection section) {
        ProxyServerInterface proxyServerInterface = new ProxyServerInterface(Leaf.getServer());

        // The message builder.
        StringBuilder builder = new StringBuilder();

        // Appear the header.
        String header = section.getString("header", null);
        if (header != null) {
            builder.append(header).append("&r\n\n");
        }

        // Get the order of the servers.
        // This list also determines which servers will be displayed.
        List<String> order = section.getListString("order", new ArrayList<>());

        // For each server in the list.
        for (String serverName : order) {

            // Get the optional server.
            Optional<RegisteredServer> server = Leaf.getServer().getServer(serverName);

            // Check if the server exists.
            if (server.isEmpty()) continue;

            // Get the information message.
            String parsed = PlaceholderManager.parse(section.getString("server"), null, new User(server.get(), null));

            // Get the amount of players online.
            int online = proxyServerInterface.getFilteredPlayers(server.get(), null, false).size();

            // Add to the final message.
            builder.append(parsed.replace("%online%", String.valueOf(online)));
            builder.append("&r\n");
        }

        // Append the footer.
        String footer = section.getString("footer", null);
        if (footer != null) {
            builder.append("&r\n");
            builder.append(section.getString("footer"));
        }

        return builder.toString();
    }
}

