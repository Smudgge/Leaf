package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;

import java.util.Objects;

/**
 * Represents the report command type.
 */
public class Report implements CommandType {

    @Override
    public String getName() {
        return "report";
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

        String message = section.getString("message")
                .replace("%message%", String.join(" ", arguments));

        message = PlaceholderManager.parse(message, null, new User(null, "Console"));

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User user = new User(player);

            user.sendMessage(message);
        }

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        String message = section.getString("message")
                .replace("%message%", String.join(" ", arguments));

        message = PlaceholderManager.parse(message, null, user);

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User toSend = new User(player);

            if (Objects.equals(toSend.getName(), user.getName())) continue;

            toSend.sendMessage(PlaceholderManager.parse(message, null, user));
        }

        user.sendMessage(message);
        MessageManager.log(message);

        return new CommandStatus();
    }
}
