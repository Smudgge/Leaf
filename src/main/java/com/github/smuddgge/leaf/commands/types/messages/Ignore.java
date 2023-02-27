package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.IgnoreRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.IgnoreTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishydatabase.Query;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * <h1>Ignore Command Type</h1>
 * Used to ignore a player.
 */
public class Ignore extends BaseCommandType {

    @Override
    public String getName() {
        return "ignore";
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

        PlayerRecord userToIgnore = playerTable.getFirstRecord(
                new Query().match("name", arguments[0])
        );

        // Check if the player is online
        if (userToIgnore == null) {
            user.sendMessage(section.getString("not_found", "{error_colour}Player is not online."));
            return new CommandStatus();
        }

        // Make sure they don't message them self's
        if (Objects.equals(arguments[0].toLowerCase(Locale.ROOT), user.getName().toLowerCase(Locale.ROOT))) {
            user.sendMessage(section.getString("ignore_self", "{error_colour}You cannot ignore yourself."));
            return new CommandStatus();
        }

        IgnoreRecord ignoreRecord = new IgnoreRecord();
        ignoreRecord.uuid = UUID.randomUUID().toString();
        ignoreRecord.playerUuid = user.getUniqueId().toString();
        ignoreRecord.ignoredPlayerUuid = userToIgnore.uuid;

        Leaf.getDatabase().getTable(IgnoreTable.class).insertRecord(ignoreRecord);

        user.sendMessage(PlaceholderManager.parse(
                section.getString("message", "{message} You are now ignoring <player>"),
                null, new User(null, userToIgnore.name)
        ));
        return new CommandStatus();
    }
}