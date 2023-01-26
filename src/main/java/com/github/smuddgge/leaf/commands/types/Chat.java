package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;

/**
 * <h1>Chat Command Type</h1>
 * Used to send a message in a chat.
 * This chat is for select players.
 */
public class Chat extends BaseCommandType {

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getSyntax() {
        return "/[name] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the message.
        String rawMessage = section.getString("format")
                .replace("%message%", String.join(" ", arguments));

        // Parse the message.
        String message = PlaceholderManager.parse(rawMessage, null, new User(null, "Console"));

        // Get the permission.
        String permission = section.getString("permission");

        // Send to players with the permission.
        for (Player player : Leaf.getServer().getAllPlayers()) {

            // Check if there is a permission
            // and if the player does not have the permission.
            if (permission != null
                    && !player.hasPermission(section.getString("permission"))) continue;

            new User(player).sendMessage(message);
        }

        // Log the message.
        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the message.
        String rawMessage = section.getString("format")
                .replace("%message%", String.join(" ", arguments));

        // Parse the message.
        String message = PlaceholderManager.parse(rawMessage, null, user);

        // Get the permission.
        String permission = section.getString("permission");

        // Send to players with the permission.
        for (Player player : Leaf.getServer().getAllPlayers()) {

            // Check if there is a permission
            // and if the player does not have the permission.
            if (permission != null
                    && !player.hasPermission(section.getString("permission"))) continue;

            new User(player).sendMessage(message);
        }

        // Log the message if it is enabled.
        if (section.getBoolean("log", true)) MessageManager.log(message);

        return new CommandStatus();
    }
}
