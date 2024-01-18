package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigDatabase;
import com.github.smuddgge.leaf.configuration.ConfigMain;
import com.github.smuddgge.leaf.database.tables.MessageTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.Sounds;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
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
            MessageManager.log(section.getAdaptedString("not_found", "\n", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        // Get the message
        String message = String.join(" ", arguments).substring(arguments[0].length());

        // Get the user that the message is being sent to.
        User recipient = new User(optionalRecipient.get());

        // Send messages
        recipient.sendMessage(PlaceholderManager.parse(section.getAdaptedString("from", "\n")
                .replace("%message%", message), null, new User(null, "Console")));

        MessageManager.log(PlaceholderManager.parse(section.getAdaptedString("to", "\n")
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

        MessageManager.log("[DEBUG] Check if muted.");

        // Check if this user is muted.
        if (user.isMuted()) {
            user.sendMessage(section.getString("you_are_muted", "&7You are muted."));
            return new CommandStatus();
        }

        MessageManager.log("[DEBUG] Checked if the player is muted.");
        MessageManager.log("[DEBUG] Check if muted 2.");

        // Check if you cannot message muted players.
        if (recipient.isMuted() && !section.getBoolean("message_muted_players", true)) {
            user.sendMessage(section.getString("recipient_muted", "&7Cannot send a message to a muted player."));
            return new CommandStatus();
        }

        MessageManager.log("[DEBUG] Checked if the player is muted 2.");

        // Get if vanishable players can message vanishable players.
        boolean allowVanishablePlayers = ConfigMain.getVanishableCanSeeVanishable();
        boolean userIsVanishable = !user.isNotVanishable();
        boolean recipientNotVanished = !recipient.isVanished();

        // Check if user is vanishable and vanishable players can message vanishable players.
        // Or check if recipient is not vanished.
        if ((allowVanishablePlayers && userIsVanishable)
                || recipientNotVanished) {

            MessageManager.log("[DEBUG] Check ignoring and can message.");

            // Check for ignoring.
            if (user.isIgnoring(recipient.getUniqueId())) {
                user.sendMessage(section.getString("ignoring", "{error_colour}You have ignored this player."));
                return new CommandStatus();
            }

            if (recipient.isIgnoring(user.getUniqueId())) {
                user.sendMessage(section.getString("recipient_ignoring", "{error_colour}This player has ignored you."));
                return new CommandStatus();
            }

            // Check for toggles.
            if (!user.canMessage()) {
                user.sendMessage(section.getString("toggled", "{error_colour}You have your messages toggled."));
                return new CommandStatus();
            }

            if (!recipient.canMessage()) {
                user.sendMessage(section.getString("recipient_toggled", "{error_colour}This player has there messages toggled."));
                return new CommandStatus();
            }

            MessageManager.log("[DEBUG] Checked if ignoring and can message.");

            // Send messages and sounds.
            recipient.sendMessage(PlaceholderManager.parse(section.getAdaptedString("from", "\n")
                    .replace("%message%", message), null, user));
            if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("from_sound"), recipient.getUniqueId());

            user.sendMessage(PlaceholderManager.parse(section.getAdaptedString("to", "\n")
                    .replace("%message%", message), null, recipient));
            if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("to_sound"), user.getUniqueId());

            MessageManager.sendSpy(section.getAdaptedString("spy_format", "\n", "&8&o%from% -> %to% : %message%")
                    .replace("%from%", user.getName())
                    .replace("%to%", recipient.getName())
                    .replace("%message%", message));

            // Spy sound.
            for (Player player : Leaf.getServer().getAllPlayers()) {
                try {
                    User temp = new User(player);
                    if (Objects.equals(temp.getRecord().toggleSeeSpy, "true")) {
                        if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("spy_sound"), temp.getUniqueId());
                    }
                } catch (Exception exception) {
                    MessageManager.log("[DEBUG] Unable to send sound for a player on message.");
                }
            }

            // Log message interaction.
            MessageManager.setLastMessaged(user.getUniqueId(), recipient.getUniqueId());

            MessageManager.log("[DEBUG] Save in database.");

            // Save to a database if enabled.
            if (!Leaf.isDatabaseDisabled()
                    && Leaf.getDatabase().isEnabled()
                    && ConfigDatabase.get().getInteger("message_limit", 0) != 0) {

                // Insert message to database.
                MessageTable messageTable = Leaf.getDatabase().getTable(MessageTable.class);
                messageTable.insertMessage(user.getUniqueId().toString(), recipient.getUniqueId().toString(), message);

                // Get message limit.
                int limit = ConfigDatabase.get().getInteger("message_limit");
                if (limit < 0) return new CommandStatus();

                // Limit old messages if the number of messages is over the limit.
                messageTable.limitMessages(user.getUniqueId().toString(), limit);

                MessageManager.log("[DEBUG] Saved in database.");
            }

            MessageManager.log("[DEBUG] Finished Debugging.");

            return new CommandStatus();
        }

        // User was not able to message this user.
        String notFound = section.getString("not_found", "{error_colour}Player is not online.");
        user.sendMessage(notFound);
        return new CommandStatus();
    }
}
