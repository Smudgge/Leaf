package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.HistoryRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.HistoryTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.events.PlayerHistoryEventType;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.ArrayList;

/**
 * Represents the history command type.
 */
public class History extends BaseCommandType {

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player] <optional page>";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (Leaf.getDatabase() == null || Leaf.getDatabase().isDisabled())
            return new CommandStatus().databaseDisabled();

        String message = this.getMessage(section, arguments);

        if (message == null) return new CommandStatus().incorrectArguments();
        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.getDatabase() == null || Leaf.getDatabase().isDisabled())
            return new CommandStatus().databaseDisabled();

        String message = this.getMessage(section, arguments);

        if (message == null) return new CommandStatus().incorrectArguments();
        user.sendMessage(message);

        return new CommandStatus();
    }

    /**
     * Used to get the history message or error.
     *
     * @param section   The configuration section.
     * @param arguments The command arguments.
     * @return The message to send to the command sender.
     * Null if there were incorrect arguments.
     */
    public String getMessage(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return null;
        String playerName = arguments[0];

        // Get database information
        PlayerTable playerTable = (PlayerTable) Leaf.getDatabase().getTable("Player");
        if (playerTable == null) return ConfigMessages.getDatabaseDisabled();

        ArrayList<Record> playerRecords = playerTable.getRecord("name", playerName);
        if (playerRecords.isEmpty()) return ConfigMessages.getDatabaseEmpty();
        PlayerRecord playerRecord = (PlayerRecord) playerRecords.get(0);

        HistoryTable historyTable = (HistoryTable) Leaf.getDatabase().getTable("History");
        if (historyTable == null) return ConfigMessages.getDatabaseDisabled();

        ArrayList<HistoryRecord> historyRecords = historyTable.getRecordOrdered("playerUuid", playerRecord.uuid);
        if (historyRecords.size() == 0) return ConfigMessages.getDatabaseEmpty();

        // Get the page information
        int pageSize = section.getInteger("page_size", 5);
        int historySize = (historyRecords.size() - 1);
        if (historySize < 0) historySize = 0;
        int amountOfPages = (historySize / section.getInteger("page_size", 5)) + 1;
        int page = 1;
        if (arguments.length > 1) {
            try {
                page = Integer.parseInt(arguments[1]);
            } catch (Exception exception) {
                return null;
            }
        }
        if (page > amountOfPages || page < 1) page = 1;

        // Build the message
        StringBuilder builder = new StringBuilder();

        String header = section.getString("header")
                .replace("%page%", String.valueOf(page))
                .replace("%page_amount%", String.valueOf(amountOfPages));

        builder.append(PlaceholderManager.parse(header, null, new User(null, playerName)));
        builder.append("\n\n");

        int index = -1;
        for (HistoryRecord historyRecord : historyRecords) {
            index += 1;
            if (index < (pageSize * (page - 1))) continue;
            if (index >= (pageSize * (page - 1)) + pageSize) continue;

            String sectionString = section.getString("section")
                    .replace("%event%", PlayerHistoryEventType.valueOf(historyRecord.event).getPrefix())
                    .replace("%server%", historyRecord.server)
                    .replace("%date%", historyRecord.date);

            builder.append(PlaceholderManager.parse(sectionString, null, new User(null, playerName)));
            builder.append("\n");
        }
        builder.append("\n");

        String footer = section.getString("footer")
                .replace("%page%", String.valueOf(page))
                .replace("%page_amount%", String.valueOf(amountOfPages));

        builder.append(PlaceholderManager.parse(footer, null, new User(null, playerName)));

        return builder.toString();
    }

    @Override
    public void loadSubCommands() {

    }
}
