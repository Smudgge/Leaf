package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;

import java.util.List;

/**
 * Represents the message command type.
 */
public class Info implements CommandType {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {

        List<String> message = section.getListString("message");

        if (message == null) {
            String messageString = section.getString("message", "null");
            MessageManager.log(messageString);

            return new CommandStatus();
        }

        StringBuilder builder = new StringBuilder();

        for (String string : message) {
            builder.append(string).append("\n");
        }

        String toSend = builder.toString();
        toSend = toSend.substring(0, toSend.length() - 2);

        MessageManager.log(toSend);

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
}
