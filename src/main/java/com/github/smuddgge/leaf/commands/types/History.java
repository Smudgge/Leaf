package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.database.records.HistoryRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.HistoryTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.listeners.PlayerHistoryEventType;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.DateAndTime;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.Query;

import java.util.ArrayList;

/**
 * <h1>History Command Type</h1>
 * Used to execute the list sub command.
 * Also acts as a parent command for the friend subcommands.
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
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Get the history message.
        String message = this.getMessage(section, arguments);

        // Check if the message is null.
        if (message == null) return new CommandStatus().incorrectArguments();

        // Log the message in console.
        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();

        // Get the history message.
        String message = this.getMessage(section, arguments);

        // Check if the message is null.
        if (message == null) return new CommandStatus().incorrectArguments();

        // Send the user the message.
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
        // Make sure they have provided an argument.
        if (arguments.length == 0) return null;

        // Get the first argument.
        String playerName = arguments[0];

        // Get database tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        HistoryTable historyTable = Leaf.getDatabase().getTable(HistoryTable.class);

        // Get the player's information.
        PlayerRecord playerRecord = playerTable.getFirstRecord(new Query().match("name", playerName));
        if (playerRecord == null) return ConfigMessages.getDatabaseEmpty();

        // Get the player's history.
        ArrayList<HistoryRecord> historyRecords = historyTable.getRecordOrdered("playerUuid", playerRecord.uuid);
        if (historyRecords.size() == 0) return ConfigMessages.getDatabaseEmpty();

        // Get the page information.
        int pageSize = section.getInteger("page_size", 5);
        int historySize = (historyRecords.size() - 1);
        if (historySize < 0) historySize = 0;

        int amountOfPages = (historySize / section.getInteger("page_size", 5)) + 1;
        int page = 1;

        // Check if a page is specified.
        if (arguments.length > 1) {
            try {
                page = Integer.parseInt(arguments[1]);
            } catch (Exception exception) {
                return null;
            }
        }

        // Check if the page is out of range.
        if (page > amountOfPages || page < 1) page = 1;

        // Build the message
        StringBuilder builder = new StringBuilder();

        // Get the header.
        String header = section.getAdaptedString("header", "\n", null);
        if (header != null) {
            builder.append(
                    PlaceholderManager.parse(header
                                    .replace("%page%", String.valueOf(page))
                                    .replace("%page_amount%", String.valueOf(amountOfPages)),
                            null, new User(null, playerName))
            );
            builder.append("\n\n");
        }

        // Get the records.
        int index = -1;
        for (HistoryRecord historyRecord : historyRecords) {
            index += 1;
            if (index < (pageSize * (page - 1))) continue;
            if (index >= (pageSize * (page - 1)) + pageSize) continue;

            String sectionString = section.getAdaptedString("section", "\n")
                    .replace("%event%", PlayerHistoryEventType.valueOf(historyRecord.event).getPrefix())
                    .replace("%server%", historyRecord.server)
                    .replace("%date%", DateAndTime.convert(historyRecord.date));

            builder.append(PlaceholderManager.parse(sectionString, null, new User(null, playerName)));
            builder.append("\n");
        }

        // Get the footer.
        String footer = section.getAdaptedString("footer", "\n", null);
        if (footer != null) {
            builder.append("\n");
            builder.append(PlaceholderManager.parse(footer
                            .replace("%page%", String.valueOf(page))
                            .replace("%page_amount%", String.valueOf(amountOfPages))
                    , null, new User(null, playerName))
            );
        }

        return builder.toString();
    }
}
