package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

import java.util.List;

/**
 * Represents the message command type.
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

        List<String> message = section.getListString("message");

        if (message == null) {
            String messageString = section.getString("message", "null");
            MessageManager.log(PlaceholderManager.parse(messageString, null, new User(null, "Console")));

            return new CommandStatus();
        }

        StringBuilder builder = new StringBuilder();

        for (String string : message) {
            builder.append(string).append("\n");
        }

        String toSend = builder.toString();
        toSend = toSend.substring(0, toSend.length() - 2);

        MessageManager.log(PlaceholderManager.parse(toSend, null, new User(null, "Console")));

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        List<String> message = section.getListString("message");

        if (message == null) {
            String messageString = section.getString("message", "null");
            user.sendMessage(messageString);

            return new CommandStatus();
        }

        StringBuilder builder = new StringBuilder();

        for (String string : message) {
            builder.append(string).append("\n");
        }

        String toSend = builder.toString();
        toSend = toSend.substring(0, toSend.length() - 2);

        user.sendMessage(toSend);

        return new CommandStatus();
    }

    @Override
    public void loadSubCommands() {

    }
}
