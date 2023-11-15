package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.database.records.IgnoreRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.IgnoreTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Ignore List Command Type</h1>
 * Used to list the players the player has ignored.
 */
public class IgnoreList extends BaseCommandType {

    @Override
    public String getName() {
        return "ignorelist";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
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

        IgnoreTable ignoreTable = Leaf.getDatabase().getTable(IgnoreTable.class);
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);

        List<IgnoreRecord> recordList = ignoreTable.getRecordList(
                new Query().match("playerUuid", user.getUniqueId().toString())
        );

        List<String> playerNames = new ArrayList<>();
        for (IgnoreRecord ignoreRecord : recordList) {
            PlayerRecord ignoredPlayerRecord = playerTable.getFirstRecord(
                    new Query().match("uuid", ignoreRecord.ignoredPlayerUuid)
            );
            assert ignoredPlayerRecord != null;

            playerNames.add(ignoredPlayerRecord.name);
        }

        String playerNamesString = String.join(", ", playerNames);
        String rawMessage = String.join("\n", section.getListString("message", new ArrayList<>()));
        String formattedMessage = rawMessage.replace("%players%", playerNamesString);

        user.sendMessage(formattedMessage);
        return new CommandStatus();
    }
}