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

import java.util.Optional;

/**
 * <h1>Find Command Type</h1>
 * Used to get information on a online player.
 */
public class Find extends BaseCommandType {

    @Override
    public String getName() {
        return "find";
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
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the player.
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(arguments[0]);

        // Check if the player doesn't exist.
        if (optionalPlayer.isEmpty()) {
            String notFound = section.getString("not_found");
            MessageManager.log(notFound);
            return new CommandStatus();
        }

        // Get the player as a user.
        User user = new User(optionalPlayer.get());

        // Log the result message.
        String found = section.getString("found");
        MessageManager.log(PlaceholderManager.parse(found, null, user));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Get the player.
        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(arguments[0]);

        // Check if the player doesn't exist.
        if (optionalPlayer.isEmpty()) {
            String notFound = section.getString("not_found");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        // Get the player as a user.
        User foundUser = new User(optionalPlayer.get());

        // If vanishable players can find vanishable players, and the user is vanishable
        if (section.getBoolean("vanishable_players", false)
                && !user.isNotVanishable()) {

            // Send the result message to the player.
            String found = section.getString("found");
            user.sendMessage(PlaceholderManager.parse(found, null, foundUser));

            return new CommandStatus();
        }

        // Check if the user is vanished.
        if (foundUser.isVanished()) {
            String notFound = section.getString("not_found");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        // Send the result message to the player.
        String found = section.getString("found");
        user.sendMessage(PlaceholderManager.parse(found, null, foundUser));

        return new CommandStatus();
    }
}
