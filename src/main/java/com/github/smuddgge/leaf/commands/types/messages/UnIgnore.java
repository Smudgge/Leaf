package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.IgnoreTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;

/**
 * <h1>UnIgnore Command Type</h1>
 * Used to unignore a player.
 */
public class UnIgnore extends BaseCommandType {

    @Override
    public String getName() {
        return "unignore";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player]";
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
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 1) return new CommandStatus().incorrectArguments();

        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        IgnoreTable ignoreTable = Leaf.getDatabase().getTable(IgnoreTable.class);

        PlayerRecord ignoredPlayer = playerTable.getFirstRecord(
                new Query().match("name", arguments[0])
        );

        // Check if the player exists in the database.
        if (ignoredPlayer == null) {
            user.sendMessage(section.getString("not_found", "{error_colour}You are not ignoring this player."));
            return new CommandStatus();
        }

        int amount = ignoreTable.getAmountOfRecords(new Query()
                .match("playerUuid", user.getUniqueId())
                .match("ignoredPlayerUuid", ignoredPlayer.uuid)
        );

        // If the player isn't ignoring the player.
        if (amount == 0) {
            user.sendMessage(section.getString("not_found", "{error_colour}You are not ignoring this player."));
            return new CommandStatus();
        }

        // Remove records.
        ignoreTable.removeAllRecords(new Query()
                .match("playerUuid", user.getUniqueId())
                .match("ignoredPlayerUuid", ignoredPlayer.uuid)
        );

        user.sendMessage(PlaceholderManager.parse(
                section.getString("message", "{message} You are no longer ignoring <player>"),
                null, new User(null, ignoredPlayer.name)
        ));
        return new CommandStatus();
    }
}