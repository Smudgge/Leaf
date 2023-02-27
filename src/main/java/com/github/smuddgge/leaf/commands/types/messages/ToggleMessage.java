package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;

/**
 * <h1>Toggle Messages Command Type</h1>
 * Used to toggle receive and send messages.
 */
public class ToggleMessage extends BaseCommandType {

    @Override
    public String getName() {
        return "togglemessages";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Toggle the players messages.
        PlayerRecord playerRecord = user.getRecord();
        playerRecord.toggleBoolean("toggleMessages");

        // Update in the database.
        Leaf.getDatabase().getTable(PlayerTable.class).insertRecord(playerRecord);

        // Send message.
        user.sendMessage(section.getString("message", "{message} Toggled messages %toggle%")
                .replace("%toggle%", playerRecord.toggleMessages));

        return new CommandStatus();
    }
}
