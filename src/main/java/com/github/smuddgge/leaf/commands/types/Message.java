package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Message implements CommandType {

    /**
     * List of players and who they last messaged.
     */
    private static final HashMap<UUID, UUID> lastMessaged = new HashMap<UUID, UUID>();

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return new CommandSuggestions().appendPlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Check if the player is online
        if (Leaf.getServer().getPlayer(arguments[0]).isEmpty()) {
            MessageManager.log(section.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        String message = arguments[1];
        User user = new User(Leaf.getServer().getPlayer(arguments[0]).get());

        // Send messages
        user.sendMessage(PlaceholderManager.parse(section.getString("from")
                .replace("%message%", message), null, new User(null, "Console")));

        MessageManager.log(PlaceholderManager.parse(section.getString("to")
                .replace("%message%", message), null, user));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Check if the player is online
        if (Leaf.getServer().getPlayer(arguments[0]).isEmpty()) {
            MessageManager.log(section.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        // Make sure they don't message them self's
        if (Objects.equals(arguments[0], user.getName())) {
            user.sendMessage(section.getString("message_self", "{error_colour}You cannot message yourself."));
            return new CommandStatus();
        }

        String message = arguments[1];
        User recipient = new User(Leaf.getServer().getPlayer(arguments[0]).get());

        // Send messages
        recipient.sendMessage(PlaceholderManager.parse(section.getString("from")
                .replace("%message%", message), null, user));

        user.sendMessage(PlaceholderManager.parse(section.getString("to")
                .replace("%message%", message), null, recipient));

        // Log message interaction
        Message.setLastMessaged(user.getUniqueId(), recipient.getUniqueId());

        return new CommandStatus();
    }

    /**
     * Used to set who a player last messaged.
     *
     * @param player Player that sent the message.
     * @param lastMessaged Player the message was sent to.
     */
    public static void setLastMessaged(UUID player, UUID lastMessaged) {
        Message.lastMessaged.put(player, lastMessaged);
        Message.lastMessaged.put(lastMessaged, player);
    }

    /**
     * Used to remove a player from the last messaged list.
     *
     * @param player The player to remove.
     */
    public static void removeLastMessaged(UUID player) {
        Message.lastMessaged.remove(player);
    }

    /**
     * Used to get who is messaging a player.
     *
     * @param player Player to get.
     */
    public static UUID getLastMessaged(UUID player) {
        return Message.lastMessaged.get(player);
    }
}
