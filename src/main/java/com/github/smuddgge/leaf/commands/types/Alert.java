package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

/**
 * <h1>Alert Command Type</h1>
 * Used to alert all online players with a message.
 */
public class Alert extends BaseCommandType {

    @Override
    public String getName() {
        return "alert";
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
        String message = section.getString("message")
                .replace("%message%", String.join(" ", arguments));

        // Send the message to all online players.
        for (Player player : Leaf.getServer().getAllPlayers()) {
            new User(player).sendMessage(message);
        }

        // Log the message in console.
        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        return this.onConsoleRun(section, arguments);
    }
}
