package com.github.smuddgge.leaf.commands.types.friends;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.commands.types.messages.Message;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
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
        CommandSuggestions commandSuggestions = new CommandSuggestions().appendFriends(user);
        List<String> onlineFriends = new ArrayList<>();

        for (String friendName : commandSuggestions.get().get(0)) {
            Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(friendName);
            if (optionalPlayer.isEmpty()) continue;

            User friend = new User(optionalPlayer.get());
            if (friend.isVanished()) continue;

            onlineFriends.add(friendName);
        }
        return new CommandSuggestions().append(onlineFriends);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        ConfigurationSection messageSection = section.getSection(this.getName());

        String command = arguments[0];
        String playerName = arguments[1];
        String argString = String.join(" ", arguments);
        String[] messageArgs = argString.substring(command.length() + 1).split(" ");

        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerName);

        // Check if the player is online
        if (optionalPlayer.isEmpty()) {
            user.sendMessage(messageSection.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        MessageManager.log("[DEBUG] Getting amount of friends");

        // Get number of records that both players are in.
        int amount = Leaf.getDatabase().getTable(FriendTable.class).getAmountOfRecords(
                new Query()
                        .match("playerUuid", user.getUniqueId().toString())
                        .match("friendPlayerUuid", optionalPlayer.get().getUniqueId().toString())
        );

        MessageManager.log("[DEBUG] Got amount of friends");

        // Check if the record exist.
        if (amount == 0) {
            user.sendMessage(messageSection.getString("not_friend", "{error_colour}You are not friends with this player."));
            return new CommandStatus();
        }

        // Send a message.
        return new Message().onPlayerRun(messageSection, messageArgs, user);
    }
}
