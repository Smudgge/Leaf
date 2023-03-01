package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.commands.types.messages.Message;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <h1>Friend Message Subcommand Type</h1>
 * Used to message a friend.
 */
public class FriendMessage implements CommandType {

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name] [player] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendFriends(user);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Get list of arguments.
        List<String> list = Arrays.stream(arguments).toList();

        // Remove [name]
        list.remove(0);

        // Get the player.
        String playerName = list.get(0);

        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerName);

        // Check if the player is online
        if (optionalPlayer.isEmpty()) {
            user.sendMessage(section.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        // Get amount of records that both players are in.
        int amount = Leaf.getDatabase().getTable(FriendTable.class).getAmountOfRecords(
                new Query()
                        .match("playerUuid", user.getUniqueId().toString())
                        .match("friendPlayerUuid", optionalPlayer.get().getUniqueId().toString())
        );

        // Check if the record exist.
        if (amount == 0) {
            user.sendMessage(section.getString("not_friend", "{error_colour}You are not friends with this player."));
            return new CommandStatus();
        }

        // Send a message.
        return new Message().onPlayerRun(section, list.toArray(new String[]{}), user);
    }
}
