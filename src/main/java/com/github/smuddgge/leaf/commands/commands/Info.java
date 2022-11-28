package com.github.smuddgge.leaf.commands.commands;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.Suggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.plugin.Plugin;

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
    public Suggestions getSuggestions(User user) {
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

                &8&m&l---------------------""";

        user.sendMessage(message);

        return new CommandStatus();
    }
}
