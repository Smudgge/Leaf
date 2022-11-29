package com.github.smuddgge.leaf.commands.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;

/**
 * Represents the info command.
 * <ul>
 *     <li>Used to get information about the plugin.</li>
 * </ul>
 */
public class Info extends Command {

    @Override
    public String getIdentifier() {
        return "info";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(String[] arguments) {
        String message = """
                &8&m&l------&r &a&lLeaf &8&m&l------

                &7Velocity Proxy Plugin
                &7Version &f<version>
                &7Author &fSmudge

                &8&m&l-----------------""";

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(String[] arguments, User user) {
        String message = """
                &8&m&l------&r &a&lLeaf &8&m&l------

                &7Velocity Proxy Plugin
                &7Version &f<version>
                &7Author &fSmudge

                &8&m&l-----------------""";

        user.sendMessage(message);

        return new CommandStatus();
    }
}
