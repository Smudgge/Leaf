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
 * Represents the chat command type.
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
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        String message = section.getString("format")
                .replace("%message%", String.join(" ", arguments));

        message = PlaceholderManager.parse(message, null, new User(null, "Console"));

        String permission = section.getString("permission");

        for (Player player : Leaf.getServer().getAllPlayers()) {

            if (permission != null && !player.hasPermission(section.getString("permission"))) continue;

            new User(player).sendMessage(message);
        }

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        String message = section.getString("format")
                .replace("%message%", String.join(" ", arguments));

        message = PlaceholderManager.parse(message, null, user);

        String permission = section.getString("permission");

        for (Player player : Leaf.getServer().getAllPlayers()) {

            if (permission != null && !player.hasPermission(section.getString("permission"))) continue;

            new User(player).sendMessage(message);
        }

        if (section.getBoolean("log", true)) MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public void loadSubCommands() {

    }
}
