package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Info Command Type</h1>
 * Used send information to players when executed.
 */
public class Info extends BaseCommandType {

    @Override
    public String getName() {
        return "info";
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

        // Get message as list.
        // If the message is not a list it will return null.
        List<String> message = section.getListString("message", new ArrayList<>());

        // If size 0 assume it's a string.
        if (message.size() == 0) {
            String messageString = section.getString("message", "null");
            MessageManager.log(PlaceholderManager.parse(messageString, null, new User(null, "Console")));
            return new CommandStatus();
        }

        // Otherwise, it's a list.
        StringBuilder builder = new StringBuilder();

        for (String string : message) {
            builder.append(string).append("\n");
        }

        String toSend = builder.toString();

        // Get rid of the last '\n'.
        toSend = toSend.substring(0, toSend.length() - 2);

        // Log the message.
        MessageManager.log(PlaceholderManager.parse(toSend, null, new User(null, "Console")));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        // Get message as list.
        // If the message is not a list it will return null.
        List<String> message = section.getListString("message", new ArrayList<>());

        // If null assume it's a string.
        if (message == null || message.isEmpty()) {
            String messageString = section.getString("message", "null");
            user.sendMessage(messageString);
            return new CommandStatus();
        }

        // Otherwise, it's a list.
        StringBuilder builder = new StringBuilder();

        for (String string : message) {
            builder.append(string).append("\n");
        }

        String toSend = builder.toString();

        // Get rid of the last '\n'.
        toSend = toSend.substring(0, toSend.length() -1);

        // Send the message to the user.
        user.sendMessage(toSend);

        return new CommandStatus();
    }
}
