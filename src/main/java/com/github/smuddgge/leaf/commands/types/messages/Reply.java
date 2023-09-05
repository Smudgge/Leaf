package com.github.smuddgge.leaf.commands.types.messages;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Reply Command Type</h1>
 * Used reply to the most recent conversation.
 */
public class Reply extends BaseCommandType {

    @Override
    public String getName() {
        return "reply";
    }

    @Override
    public String getSyntax() {
        return "/[name] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        MessageManager.log("{error_colour}The console cannot reply to messages.");
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 1) return new CommandStatus().incorrectArguments();

        // Get the last pearson the user has messaged.
        User recipient = user.getLastMessaged();

        // Check if the recipient doesn't exist.
        if (recipient == null) {
            user.sendMessage(section.getString("not_found", "{error_colour}You have no conversation to reply to."));
            return new CommandStatus();
        }

        // Make message arguments.
        List<String> messageArguments = new ArrayList<>();
        messageArguments.add(recipient.getName());
        Collections.addAll(messageArguments, arguments);

        Message message = new Message();
        return message.onPlayerRun(section, messageArguments.toArray(new String[0]), user);
    }
}
