package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * <h1>Message Command Type</h1>
 * Used to message players across servers.
 */
public class Message extends BaseCommandType {

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendPlayers(user);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Attempt to get the receiver.
        Optional<Player> optionalRecipient = Leaf.getServer().getPlayer(arguments[0]);

        // Check if the player is online
        if (optionalRecipient.isEmpty()) {
            MessageManager.log(section.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        // Get the message
        String message = String.join(" ", arguments).substring(arguments[0].length());

        // Get the user that the message is being sent to.
        User recipient = new User(optionalRecipient.get());

        // Send messages
        recipient.sendMessage(PlaceholderManager.parse(section.getString("from")
                .replace("%message%", message), null, new User(null, "Console")));

        MessageManager.log(PlaceholderManager.parse(section.getString("to")
                .replace("%message%", message), null, recipient));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Attempt to get the receiver.
        Optional<Player> optionalRecipient = Leaf.getServer().getPlayer(arguments[0]);

        // Check if the player is online
        if (optionalRecipient.isEmpty()) {
            user.sendMessage(section.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        // Make sure they don't message them self's
        if (Objects.equals(arguments[0].toLowerCase(Locale.ROOT), user.getName().toLowerCase(Locale.ROOT))) {
            user.sendMessage(section.getString("message_self", "{error_colour}You cannot message yourself."));
            return new CommandStatus();
        }

        // Get the message.
        String message = String.join(" ", arguments).substring(arguments[0].length()).trim();

        // Get who the message is being sent to.
        User recipient = new User(optionalRecipient.get());

        // Get if vanishable players can message vanishable players.
        boolean allowVanishablePlayers = ConfigCommands.canVanishableSeeVanishable();
        boolean userIsVanishable = !user.isNotVanishable();
        boolean recipientNotVanished = !recipient.isVanished();

        // Check if user is vanishable and vanishable players can message vanishable players.
        // Or check if recipient is not vanished.
        if ((allowVanishablePlayers && userIsVanishable)
                || recipientNotVanished) {

            // Send messages
            recipient.sendMessage(PlaceholderManager.parse(section.getString("from")
                    .replace("%message%", message), null, user));

            user.sendMessage(PlaceholderManager.parse(section.getString("to")
                    .replace("%message%", message), null, recipient));

            // Log message interaction
            MessageManager.setLastMessaged(user.getUniqueId(), recipient.getUniqueId());
            return new CommandStatus();
        }

        // User was not able to message this user.
        String notFound = section.getString("not_found", "{error_colour}Player is not online.");
        user.sendMessage(notFound);
        return new CommandStatus();
    }
}
