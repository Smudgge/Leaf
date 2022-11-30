package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

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

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User user = new User(player);
            user.sendMessage(section.getString("message")
                    .replace("%player%", user.getName())
                    .replace("%message", String.join(" ", arguments)));
        }

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        return this.onConsoleRun(section, arguments);
    }
}
