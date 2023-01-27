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

import java.util.Objects;

/**
 * <h1>Report Command Type</h1>
 * Used to message a selection of players
 * with a certain permission.
 */
public class Report extends BaseCommandType {

    @Override
    public String getName() {
        return "report";
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
        String rawMessage = section.getString("message")
                .replace("%message%", String.join(" ", arguments));

        // Parse the message.
        String message = PlaceholderManager.parse(rawMessage, null, new User(null, "Console"));

        // Get the permission to see the report
        String permissionSee = section.getString("see_report", null);

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User user = new User(player);

            // Check if a permission is required and the player has the permission.
            if (permissionSee != null && !user.hasPermission(permissionSee)) continue;

            // Send the message.
            user.sendMessage(message);
        }

        // Log the message in console.
        MessageManager.log(message);
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the message.
        String rawMessage = section.getString("message")
                .replace("%message%", String.join(" ", arguments));

        // Parse the message.
        String message = PlaceholderManager.parse(rawMessage, null, user);

        // Get the permission to see the report
        String permissionSee = section.getString("see_report", null);

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User toSend = new User(player);

            // Check if it is the same player as the sender.
            if (Objects.equals(toSend.getName(), user.getName())) continue;

            // Check if a permission is required and the player has the permission.
            if (permissionSee != null && !user.hasPermission(permissionSee)) continue;

            // Send the message.
            toSend.sendMessage(message);
        }

        // Send the message to the sender.
        user.sendMessage(message);

        // Log the message.
        MessageManager.log(message);
        return new CommandStatus();
    }
}
