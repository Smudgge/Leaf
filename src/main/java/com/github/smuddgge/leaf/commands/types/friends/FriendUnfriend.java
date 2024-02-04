package com.github.smuddgge.leaf.commands.types.friends;

import com.github.smuddgge.leaf.FriendManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;

/**
 * <h1>Friend Accept Subcommand Type</h1>
 * Used to unfriend a player in there friend list.
 */
public class FriendUnfriend implements CommandType {

    @Override
    public String getName() {
        return "unfriend";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name]";
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
        if (arguments.length <= 1) return new CommandStatus().incorrectArguments();

        // Get the unfriend configuration section.
        ConfigurationSection unfriendSection = section.getSection(this.getName());

        // Get the player name to unfriend.
        String unfriendPlayerName = arguments[1];

        // Get database tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        FriendTable friendTable = Leaf.getDatabase().getTable(FriendTable.class);

        // Get the unfriend players information.
        PlayerRecord unfriendPlayerRecord = playerTable.getFirstRecord(new Query().match("name", unfriendPlayerName));
        if (unfriendPlayerRecord == null) {
            user.sendMessage(unfriendSection.getString("not_found", "{error_colour}Invalid player name."));
            return new CommandStatus();
        }

        // Get the friend record of the two players.
        FriendRecord friendRecord = friendTable.getFriend(user.getUniqueId().toString(), unfriendPlayerRecord.uuid);

        // Check if the player is not a friend.
        if (friendRecord == null) {
            user.sendMessage(unfriendSection.getString("not_found", "{error_colour}Invalid player name."));
            return new CommandStatus();
        }

        // Unfriend player.
        FriendManager.unFriend(user.getUniqueId().toString(), unfriendPlayerRecord.uuid);

        // Send the user a message.
        String message = unfriendSection.getString("message", "{message} You are no longer friends with <player>");
        user.sendMessage(PlaceholderManager.parse(message, null, new User(null, unfriendPlayerRecord.name)));

        return new CommandStatus();
    }
}
