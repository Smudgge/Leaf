package com.github.smuddgge.leaf.commands.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

/**
 * Represents the alert command.
 * <ul>
 *     <li>Used to alert all online players with a given message.</li>
 * </ul>
 */
public class Alert extends Command {

    @Override
    public String getIdentifier() {
        return "alert";
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
    public CommandStatus onConsoleRun(String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        String message = ConfigCommands.getCommand(this.getIdentifier()).getString("message")
                .replace("%message%", String.join(" ", arguments));

        for (Player player : Leaf.getServer().getAllPlayers()) {
            new User(player).sendMessage(message);
        }

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(String[] arguments, User user) {
        return this.onConsoleRun(arguments);
    }
}