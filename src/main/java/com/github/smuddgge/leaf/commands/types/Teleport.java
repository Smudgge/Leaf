package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;

/**
 * <h1>Teleport Command Type</h1>
 * Used to teleport to the server a player is on.
 */
public class Teleport extends BaseCommandType {

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

        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(arguments[0]);

        // Check if the player is online.
        if (optionalPlayer.isEmpty()) {
            String notFound = section.getString("not_found", "{error_colour}Player could not be found.");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        // Get the player as a user.
        User foundUser = new User(optionalPlayer.get());

        // Get if vanishable players can message vanishable players.
        boolean allowVanishablePlayers = ConfigMain.getVanishableCanSeeVanishable();
        boolean userIsVanishable = !user.isNotVanishable();
        boolean recipientVanished = foundUser.isVanished();

        // Check if user is vanishable and vanishable players can message vanishable players.
        // Or check if recipient is not vanished.
        if ((allowVanishablePlayers && userIsVanishable)
                || recipientVanished) {

            String notFound = section.getString("not_found", "{error_colour}Player could not be found.");
            user.sendMessage(notFound);
            return new CommandStatus();
        }

        // Get the message and send it.
        String message = section.getString("message", "{message} Teleporting...");

        user.sendMessage(message);
        user.teleport(foundUser.getConnectedServer());

        return new CommandStatus();
    }
}
