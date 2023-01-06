package com.github.smuddgge.leaf.commands.subtypes.friends;

import com.github.smuddgge.leaf.FriendRequestManager;
import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class Request implements CommandType {

    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getSyntax() {
        return "/[parent] [name] [player] <optional message>";
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

        String playerNameToRequest = arguments[1];

        if (user.getName().toLowerCase(Locale.ROOT).equals(playerNameToRequest.toLowerCase(Locale.ROOT))) {
            user.sendMessage(section.getSection("request").getString("self", "{error_colour}You can not be friends with your self."));
            return new CommandStatus();
        }

        if (Leaf.getDatabase().isDisabled()) return new CommandStatus().databaseDisabled();

        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");

        ArrayList<Record> playerResults = playerTable.getRecord("name", playerNameToRequest);
        if (playerResults.size() == 0) {
            user.sendMessage(section.getSection("request").getString("not_found", "{error_colour}Player has never played on this server."));
            return new CommandStatus();
        }

        PlayerRecord playerRecordToRequest = (PlayerRecord) playerResults.get(0);

        boolean success = FriendRequestManager.sendRequest(user, playerRecordToRequest);

        if (!success) return new CommandStatus().databaseEmpty();

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
