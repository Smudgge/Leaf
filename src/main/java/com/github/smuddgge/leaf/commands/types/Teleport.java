package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;

public class Teleport extends BaseCommandType {

    @Override
    public void loadSubCommands() {

    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendPlayers(user);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        if (Leaf.getServer().getPlayer(arguments[0]).isEmpty()) {
            String notFound = section.getString("not_found", "{error_colour}Player could not be found.");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        User foundUser = new User(Leaf.getServer().getPlayer(arguments[0]).get());

        // If vanishable players can teleport to other vanishable players, and the user is vanishable
        if (section.getBoolean("vanishable_players", false) && !user.isNotVanishable()) {
            String message = section.getString("message", "{message} Teleporting...");

            user.sendMessage(message);
            user.teleport(foundUser.getConnectedServer());

            return new CommandStatus();
        }

        if (foundUser.isVanished()) {
            String notFound = section.getString("not_found", "{error_colour}Player could not be found.");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        String message = section.getString("message", "{message} Teleporting...");

        user.sendMessage(message);
        user.teleport(foundUser.getConnectedServer());

        return new CommandStatus();
    }
}
