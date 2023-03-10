package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.ConfigMessages;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.records.MessageRecord;
import com.github.smuddgge.leaf.database.tables.MessageTable;
import com.github.smuddgge.leaf.database.tables.PlayerTable;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.leaf.utility.DateAndTime;
import com.github.smuddgge.squishydatabase.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageHistory extends BaseCommandType {

    @Override
    public String getName() {
        return "messagehistory";
    }

    @Override
    public String getSyntax() {
        return "/[name] [query]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        List<String> options = new ArrayList<>();

        // Player options.
        CommandSuggestions temp = new CommandSuggestions().appendDatabasePlayers();
        for (String name : temp.get().get(0)) {
            options.add("p:" + name);
        }

        options.add("t:");
        options.add("i:\"");
        options.add("e:\"");

        return new CommandSuggestions().append(options).setContinuous();
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 1) return new CommandStatus().incorrectArguments();

        String message = this.getMessage(section, arguments);

        if (message == null) return new CommandStatus().incorrectArguments();

        MessageManager.log(message);

        return new CommandStatus().playerCommand();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (Leaf.isDatabaseDisabled()) return new CommandStatus().databaseDisabled();
        if (arguments.length < 1) return new CommandStatus().incorrectArguments();

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
        // Get database tables.
        PlayerTable playerTable = Leaf.getDatabase().getTable(PlayerTable.class);
        MessageTable messageTable = Leaf.getDatabase().getTable(MessageTable.class);

        // Get the message history.
        List<MessageRecord> messageRecordList = messageTable.getMessagesOrdered(String.join(" ", arguments));
        if (messageRecordList.size() == 0) return ConfigMessages.getDatabaseEmpty();

        // Get the page information.
        int pageSize = section.getInteger("page_size", 5);
        int messageSize = (messageRecordList.size() - 1);
        if (messageSize < 0) messageSize = 0;

        int amountOfPages = (messageSize / section.getInteger("page_size", 5)) + 1;
        int page = 1;

        // Check if a page is specified.
        for (String item : arguments) {
            try {
                page = Integer.parseInt(item);
            } catch (Exception ignored) {
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

        builder.append(header);
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
                    .replace("%date%", DateAndTime.convert(messageRecord.date));

            builder.append(PlaceholderManager.parse(sectionString, null, new User(null, from)));
            builder.append("\n");
        }
        builder.append("\n");

        // Get the footer.
        String footer = section.getString("footer")
                .replace("%page%", String.valueOf(page))
                .replace("%page_amount%", String.valueOf(amountOfPages));

        builder.append(footer);

        return builder.toString();
    }
}