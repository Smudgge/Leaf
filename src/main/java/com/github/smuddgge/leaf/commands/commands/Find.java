package com.github.smuddgge.leaf.commands.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

/**
 * Represents the find command.
 * <ul>
 *     <li>Used to find a player and get information on them.</li>
 * </ul>
 */
public class Find extends Command {

    @Override
    public String getIdentifier() {
        return "find";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return new CommandSuggestions().appendPlayers();
    }

    @Override
    public CommandStatus onConsoleRun(String[] arguments) {

        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        if (Leaf.getServer().getPlayer(arguments[0]).isEmpty()) {

            String notFound = ConfigCommands.getCommand(this.getIdentifier()).getString("not_found");

            MessageManager.log(notFound);

            return new CommandStatus();
        }

        User user = new User(Leaf.getServer().getPlayer(arguments[0]).get());

        String found = ConfigCommands.getCommand(this.getIdentifier()).getString("found");

        MessageManager.log(PlaceholderManager.parse(found, null, user));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(String[] arguments, User user) {

        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        if (Leaf.getServer().getPlayer(arguments[0]).isEmpty()) {

            String notFound = ConfigCommands.getCommand(this.getIdentifier()).getString("not_found");

            user.sendMessage(notFound);

            return new CommandStatus();
        }

        User foundUser = new User(Leaf.getServer().getPlayer(arguments[0]).get());

        String found = ConfigCommands.getCommand(this.getIdentifier()).getString("found");

        user.sendMessage(PlaceholderManager.parse(found, null, foundUser));

        return new CommandStatus();
    }
}
