package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.FriendManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.FriendRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.ArrayList;
import java.util.List;

public class UnFriend implements CommandType {

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
        if (Leaf.getDatabase().isDisabled()) return null;

        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");
        ArrayList<Record> friends = friendTable.getRecord("playerUuid", user.getUniqueId());

        List<String> friendList = new ArrayList<>();
        for (Record record : friends) {
            FriendRecord friendRecord = (FriendRecord) record;
            friendList.add(friendRecord.friendNameFormatted);
        }

        return new CommandSuggestions().append(friendList);
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.getDatabase().isDisabled()) return new CommandStatus().databaseDisabled();

        if (arguments.length <= 1) return new CommandStatus().incorrectArguments();

        // Get the player name to unfriend.
        String playerName = arguments[1];

        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");
        FriendTable friendTable = (FriendTable) Leaf.getDatabase().getTable("Friend");

        // Player has never logged on.
        if (!playerTable.contains(playerName)) {
            user.sendMessage(section.getString("not_found", "{error_colour}Invalid player name."));
            return new CommandStatus();
        }

        ArrayList<Record> results = playerTable.getRecord("name", user.getName());
        PlayerRecord playerRecord = (PlayerRecord) results.get(0);

        FriendRecord friendRecord = friendTable.getFriend(user.getUniqueId().toString(), playerRecord.uuid);

        // Player is not their friend.
        if (friendRecord == null) {
            user.sendMessage(section.getString("not_found", "{error_colour}Invalid player name."));
            return new CommandStatus();
        }

        // Unfriend player.
        FriendManager.unFriend(user.getUniqueId().toString(), playerRecord.uuid);

        String message = section.getString("message", "{message} You are no longer friends with <player>");
        user.sendMessage(PlaceholderManager.parse(message, null, new User(null, playerRecord.name)));

        return new CommandStatus();
    }
}
