package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.HistoryRecord;
import com.github.smuddgge.leaf.database.records.IgnoreRecord;
import com.github.smuddgge.leaf.database.records.MessageRecord;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.database.tables.HistoryTable;
import com.github.smuddgge.leaf.database.tables.IgnoreTable;
import com.github.smuddgge.leaf.database.tables.MessageTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.events.PlayerHistoryEventType;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishydatabase.Query;

import java.util.*;

public class MessageHistory extends BaseCommandType {

    @Override
    public String getName() {
        return "messagehistory";
    }

    @Override
    public String getSyntax() {
        return "/[name] [player] [player]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return new CommandSuggestions().appendDatabasePlayers().appendDatabasePlayers();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        String message = this.getMessage(section, arguments);

        if (message == null) return new CommandStatus().incorrectArguments();

        MessageManager.log(message);

        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 2) return new CommandStatus().incorrectArguments();

        String message = this.getMessage(section, arguments);

        if (message == null) return new CommandStatus().incorrectArguments();

        user.sendMessage(message);

        return new CommandStatus();
    }

    /**
     * Used to get the message.
     *
     * @param section   The configuration section.
     * @param arguments The command arguments.
     * @return The message to send to the command sender.
     * Null if there were incorrect arguments.
     */
    public String getMessage(ConfigurationSection section, String[] arguments) {
        // Get the player names.
        String playerName1 = arguments[0];
        String playerName2 = arguments[1];

        // Get database tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        MessageTable messageTable = Leaf.getDatabase().getTable(MessageTable.class);

        // Get the player's information.
        PlayerRecord playerRecord1 = playerTable.getFirstRecord(new Query().match("name", playerName1));
        PlayerRecord playerRecord2 = playerTable.getFirstRecord(new Query().match("name", playerName2));

        // Check if the players exist.
        if (playerRecord1 == null || playerRecord2 == null) return null;

        // Get the message history.
        List<MessageRecord> messageRecordList = messageTable.getMessagesOrdered(playerRecord1.uuid, playerRecord2.uuid);
        if (messageRecordList.size() == 0) return ConfigMessages.getDatabaseEmpty();

        // Get the page information.
        int pageSize = section.getInteger("page_size", 5);
        int messageSize = (messageRecordList.size() - 1);
        if (messageSize < 0) messageSize = 0;

        int amountOfPages = (messageSize / section.getInteger("page_size", 5)) + 1;
        int page = 1;

        // Check if a page is specified.
        if (arguments.length > 2) {
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
        String header = section.getString("header")
                .replace("%page%", String.valueOf(page))
                .replace("%page_amount%", String.valueOf(amountOfPages));

        builder.append(PlaceholderManager.parse(header, null, new User(null, playerName1)));
        builder.append("\n\n");

        // Get the records.
        int index = -1;
        for (MessageRecord messageRecord : messageRecordList) {
            index += 1;
            if (index < (pageSize * (page - 1))) continue;
            if (index >= (pageSize * (page - 1)) + pageSize) continue;

            String from = Objects.requireNonNull(
                    playerTable.getFirstRecord(new Query().match("uuid", messageRecord.fromPlayerUuid))
            ).name;

            String to = Objects.requireNonNull(
                    playerTable.getFirstRecord(new Query().match("uuid", messageRecord.toPlayerUuid))
            ).name;

            String sectionString = section.getString("section")
                    .replace("%message%", messageRecord.message)
                    .replace("%from%", from)
                    .replace("%to%", to)
                    .replace("%date%", messageRecord.date);

            builder.append(PlaceholderManager.parse(sectionString, null, new User(null, playerName1)));
            builder.append("\n");
        }
        builder.append("\n");

        // Get the footer.
        String footer = section.getString("footer")
                .replace("%page%", String.valueOf(page))
                .replace("%page_amount%", String.valueOf(amountOfPages));

        builder.append(PlaceholderManager.parse(footer, null, new User(null, playerName1)));

        return builder.toString();
    }
}