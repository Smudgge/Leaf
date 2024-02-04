package com.github.smuddgge.leaf.commands.types.friends;

import com.github.smuddgge.leaf.FriendManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.database.records.FriendSettingsRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.FriendSettingsTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;
import com.velocitypowered.api.proxy.Player;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * <h1>Friend Request Subcommand Type</h1>
 * Used to create a friend request to a player.
 */
public class FriendRequest implements CommandType {

    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name] [player]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        // Get the request configuration section.
        ConfigurationSection requestSection = section.getSection("request");

        // Get the given argument.
        String playerNameToRequest = arguments[1];

        // Check if they are creating a request to them self.
        if (user.getName().toLowerCase(Locale.ROOT).equals(playerNameToRequest.toLowerCase(Locale.ROOT))) {
            user.sendMessage(requestSection.getString("self", "{error_colour}You can not be friends with your self."));
            return new CommandStatus();
        }

        // Check if the database is disabled.
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

        // Get player to requests information.
        PlayerRecord playerToRequestRecord = playerTable.getFirstRecord(new Query().match("name", playerNameToRequest));
        if (playerToRequestRecord == null) {
            user.sendMessage(requestSection.getString("not_found", "{error_colour}Player has never played on this server."));
            return new CommandStatus();
        }

        // Get the settings of the player to request.
        FriendSettingsTable friendSettingsTable = Leaf.getDatabase().getTable(FriendSettingsTable.class);
        FriendSettingsRecord playerToRequestSettings = friendSettingsTable.getSettings(playerToRequestRecord.uuid);

        // Check if they have their friend requests toggled false.
        if (Objects.equals(playerToRequestSettings.toggleRequests, "false")) {
            user.sendMessage(section.getSection("request").getString("requests_off", "{error_colour}This player has there friend requests off."));
            return new CommandStatus();
        }

        // Check if the player has already sent a friend request to this player.
        if (FriendManager.hasRequested(user.getUniqueId(), playerToRequestRecord.uuid)) {
            user.sendMessage(section.getSection("request").getString("already_requested", "{error_colour}You have already requested to be friends with this player."));
            return new CommandStatus();
        }

        // Check if the player that has been requested has already sent a request.
        if (FriendManager.hasRequested(UUID.fromString(playerToRequestRecord.uuid), user.getUniqueId().toString())) {
            user.sendMessage(section.getSection("request").getString("has_request", "{error_colour}This player has already sent you a request."));
            return new CommandStatus();
        }

        // Check if they are already friends.
        if (user.isFriends(UUID.fromString(playerToRequestRecord.uuid))) {
            user.sendMessage(section.getSection("request").getString("already_friends", "{error_colour}You are already friends with this player."));
            return new CommandStatus();
        }

        // Send a friend request anonymously.
        boolean success = FriendManager.sendRequest(user, playerToRequestRecord);
        if (!success) return new CommandStatus().databaseEmpty();

        // Send the players a message.
        user.sendMessage(PlaceholderManager.parse(
                section.getSection("request").getString("sent"),
                null, new User(null, playerNameToRequest)
        ));

        Optional<Player> optionalPlayer = Leaf.getServer().getPlayer(playerNameToRequest);
        if (optionalPlayer.isEmpty()) return new CommandStatus();

        User userSentTo = new User(optionalPlayer.get());

        userSentTo.sendMessage(PlaceholderManager.parse(
                section.getSection("request").getString("from"),
                null, user
        ));

        return new CommandStatus();
    }
}
